package project.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.{ActorMaterializer}
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import project.domain.HistoryItem
import project.domain.dto.HistoryItemDto
import project.service.HistoryService
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Handler {

  case class CreateHistory(book: HistoryItemDto)
  case class GetPhotoByID(photoId: String)
  case class DeleteHistoryByID(id: String)
  case class GetHistoryByID(id: String)
  case class GetAll()

  def props(elasticClient: ElasticClient)(
    implicit ec: ExecutionContext,
    actorSystem: ActorSystem,
    config: Config,
    materializer: ActorMaterializer,
  ): Props = Props(new Handler(elasticClient))

}

class Handler(elasticClient: ElasticClient)(
  implicit ec: ExecutionContext,
  materializer: ActorMaterializer,
  actorSystem: ActorSystem,
  config: Config
) extends Actor with ActorLogging {

  def historyService: HistoryService = new HistoryService(elasticClient)

  def receive: Receive = {

    case Handler.CreateHistory(historyItemDto) =>

      val history = HistoryItem(
        id = UUID.randomUUID().toString,
        filter = historyItemDto.filter,
        responce = historyItemDto.responce,
        exception = historyItemDto.exception
      )

      historyService.createHistory(history).onComplete {
        case Success(history) =>
          historyService.searchPhotos(history.filter).onComplete {
            case Success(value) =>
              historyService.updateHistory(HistoryItem(
                history.id,
                filter = history.filter,
                responce = Some(value),
                exception = history.exception)
              ).onComplete {
                case Success(history) => context.parent ! history
                case Failure(exception) => context.parent ! exception
              }
            case Failure(exception: Throwable) =>
              historyService.updateHistory(HistoryItem(
                history.id,
                filter = history.filter,
                responce = history.responce,
                exception = Some(exception))
              ).onComplete {
                case Success(history) => context.parent ! history
                case Failure(exception) => throw exception
              }
            case Failure(exception) => throw exception
          }
        case Failure(exception) => throw exception
      }

    case Handler.GetPhotoByID(id) =>
      val history = HistoryItem(
        id = UUID.randomUUID().toString,
        filter = id,
        responce = null,
        exception = null
      )

      historyService.createHistory(history).onComplete {
        case Success(history) =>
          historyService.searchPhotoByID(id).onComplete {
            case Success(value) =>
              historyService.updateHistory(HistoryItem(
                history.id,
                filter = history.filter,
                responce = Some(value),
                exception = history.exception)
              ).onComplete {
                case Success(history) => context.parent ! history
                case Failure(exception) => context.parent ! exception
              }
            case Failure(exception: Throwable) =>
              historyService.updateHistory(HistoryItem(
                history.id,
                filter = history.filter,
                responce = history.responce,
                exception = Some(exception))
              ).onComplete {
                case Success(history) => context.parent ! history
                case Failure(exception) => throw exception
              }
            case Failure(exception) => throw exception
          }
        case Failure(exception) => throw exception
      }

    case Handler.GetHistoryByID(id) =>
      historyService.getHistoryByID(id).onComplete {
        case Success(res) =>
          res match {
            case Some(book) => context.parent ! book
            case None => context.parent ! "History with this id does not exist"
          }
        case Failure(exception) => throw exception
      }

    case Handler.GetAll() =>
      historyService.getAll().onComplete {
        case Success(books) => context.parent ! books
        case Failure(exception) => throw exception
      }

    case Handler.DeleteHistoryByID(id) =>
      historyService.deleteByID(id).onComplete {
        case Success(res) => context.parent ! "Deleted"
        case Failure(exception) => throw exception
      }
  }
}
