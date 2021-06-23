package project.route

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RouteResult}
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import project.actors.Handler
import project.domain.dto.HistoryItemDto
import project.serializer.Serializer

import scala.concurrent.Promise

trait Routes extends PerRequestCreator with Serializer with BasicDirectives with Json4sSupport {

  implicit val actorSystem: ActorSystem
  implicit val timeout: Timeout

  def handlerProps: Props

  val routes: Route =
    pathPrefix("history") {
      concat(
        path("api") {
          get {
            parameter('filter.as[String]) { filter =>
              handle(handlerProps, Handler.CreateHistory(HistoryItemDto(filter = filter, responce = null, exception = null)))
            }
          }
        },
        path(Segment) { value =>
          value match {
            case "all" =>
              get {
                handle(handlerProps, Handler.GetAll())
              }
            case _ =>
              get {
                handle(handlerProps, Handler.GetHistoryByID(value))
              }
          }
        },
        path(Segment) { bookId =>
          delete {
            handle(handlerProps, Handler.DeleteHistoryByID(bookId))
          }
        }
      )
    }

  def handle(props: Props, message: Any): Route = ctx => {
    val p = Promise[RouteResult]
    handleRequest(ctx, props, message, p)
    p.future
  }

}
