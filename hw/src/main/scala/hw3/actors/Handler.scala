package hw3.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import hw3.domain.Book
import hw3.domain.dto.BookDto
import hw3.service.BookService

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Handler {

  case class CreateBook(book: BookDto)

  case class DeleteBookByID(id: String)

  case class UpdateBook(id: String, newBook: BookDto)

  case class GetBooksByAuthor(author: String)

  case class GetBookByID(id: String)

  case class GetAll()

  def props(elasticClient: ElasticClient)(
    implicit ec: ExecutionContext,
    config: Config
  ): Props = Props(new Handler(elasticClient))

}

class Handler(elasticClient: ElasticClient)(
  implicit ec: ExecutionContext,
  config: Config
) extends Actor with ActorLogging {

  def bookService: BookService = new BookService(elasticClient)

  def receive: Receive = {
    case Handler.CreateBook(bookDto) =>
      val book = Book(id = UUID.randomUUID().toString, name = bookDto.name, author = bookDto.author)
      bookService.createBook(book).onComplete {
        case Success(book) =>
          context.parent ! book
        case Failure(exception) =>
          throw exception
      }

    case Handler.UpdateBook(id, bookDto) =>
      val newBook = Book(id = id, name = bookDto.name, author = bookDto.author)
      bookService.updateBook(newBook).onComplete {
        case Success(book) =>
          context.parent ! book
        case Failure(exception) =>
          throw exception
      }

    case Handler.GetBooksByAuthor(author) =>
      bookService.searchBooksByAuthor(author).onComplete {
        case Success(books) =>
          context.parent ! books
        case Failure(exception) =>
          throw exception
      }

    case Handler.GetBookByID(id) =>
      bookService.searchBookByID(id).onComplete {
        case Success(res) =>
          res match {
            case Some(book) => context.parent ! book
            case None => context.parent ! "Book with this id does not exist"
          }
        case Failure(exception) =>
          throw exception
      }

    case Handler.GetAll() =>
      bookService.getAll().onComplete {
        case Success(books) =>
          context.parent ! books
        case Failure(exception) =>
          throw exception
      }

    case Handler.DeleteBookByID(id) =>
      bookService.deleteByID(id).onComplete {
        case Success(res) =>
          context.parent ! "Deleted"
        case Failure(exception) =>
          throw exception
      }
  }
}