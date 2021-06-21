package hw3.route

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RouteResult}
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import hw3.actors.Handler
import hw3.domain.Book
import hw3.domain.dto.BookDto
import hw3.serializer.Serializer

import scala.concurrent.Promise

trait Routes extends PerRequestCreator with Serializer with BasicDirectives with Json4sSupport {

  implicit val actorSystem: ActorSystem
  implicit val timeout: Timeout

  def handlerProps: Props

  val routes: Route =
    pathPrefix("library") {
      concat(
        path("create-book") {
          post { //POST localhost:6060/library/create-book
            entity(as[BookDto]) { entity =>
              handle(handlerProps, Handler.CreateBook(entity))
            }
          }
        },
        path(Segment) { bookId =>
          bookId match {
            case "all" =>
              get { // GET localhost:6060/library/all
                handle(handlerProps, Handler.GetAll())
              }
            case "get-by-author" =>
              get { // GET localhost:6060/library/get-by-author with query params
                parameter('author.as[String]) { author =>
                  handle(handlerProps, Handler.GetBooksByAuthor(author))
                }
              }
            case _ =>
              get { //GET http://localhost:6060/library/bookId
                handle(handlerProps, Handler.GetBookByID(bookId))
              }
          }
        },
        path(Segment) { bookId =>
          delete { //DELETE http://localhost:6060/library/bookId
            handle(handlerProps, Handler.DeleteBookByID(bookId))
          }
        },
        path(Segment) { bookId =>
          put { //PUT localhost:6060/library/bookId with new book in body
            entity(as[BookDto]) { entity =>
              handle(handlerProps, Handler.UpdateBook(bookId, entity))
            }
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

