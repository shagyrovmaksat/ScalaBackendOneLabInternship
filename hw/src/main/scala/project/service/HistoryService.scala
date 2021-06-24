package project.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import org.json4s.jackson.JsonMethods
import project.Boot.formats
import project.domain.{HistoryItem, Photo, SearchResult}
import project.repository.HistoryRepository

import java.net.URI
import java.nio.charset.StandardCharsets
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

class HistoryService(client: ElasticClient)(
  implicit config: Config,
  actorSystem: ActorSystem,
  ec: ExecutionContext,
  materializer: ActorMaterializer){

  private def historyRepository: HistoryRepository = new HistoryRepository(client)

  val clientId = "BZm-e7lllqdKs7AUyU67ybIApL1EUmWK45j2pulc_lQ"

  def getAll(): Future[Seq[HistoryItem]] =
    historyRepository.search(
      historyRepository.search(
        historyRepository.indexName
      )
    )

  def getHistoryByID(id: String): Future[Option[HistoryItem]] = historyRepository.find(id)

  def createHistory(history: HistoryItem): Future[HistoryItem] = historyRepository.upsert(history.id, history)

  def updateHistory(newHistory: HistoryItem): Future[HistoryItem] = historyRepository.upsert(newHistory.id, newHistory)

  def deleteByID(id: String) = historyRepository.deleteById(id)

  def searchPhotos(filter: String): Future[Seq[Photo]] = {
    val p = Promise[Seq[Photo]]

    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = URI.create(s"https://api.unsplash.com/search/photos?query=$filter&client_id=$clientId").normalize.toString
      )
    ).onComplete {
      case Success(value) =>
        value.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(_.decodeString(StandardCharsets.UTF_8)).map(JsonMethods.parse(_).extract[SearchResult]).onComplete {
          case Success(value) =>
            p.success(value.results)
          case Failure(exception) =>
            p.failure(exception)
        }
      case Failure(exception) =>
        p.failure(exception)
    }

    p.future
  }

  def searchPhotoByID(id: String): Future[Seq[Photo]] = {
    val p = Promise[Seq[Photo]]

    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = URI.create(s"https://api.unsplash.com/photos/$id?client_id=$clientId").normalize.toString
      )
    ).onComplete {
      case Success(value) =>
        value.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(_.decodeString(StandardCharsets.UTF_8)).map(JsonMethods.parse(_).extract[Photo]).onComplete {
          case Success(value) =>
            p.success(Seq(value))
          case Failure(exception) =>
            p.failure(exception)
        }
      case Failure(exception) =>
        p.failure(exception)
    }

    p.future
  }

}
