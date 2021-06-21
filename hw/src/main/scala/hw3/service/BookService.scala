package hw3.service

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.requests.searches.queries.term.TermQuery
import com.typesafe.config.Config
import hw3.domain.Book
import hw3.repository.BookRepository

import scala.concurrent.{ExecutionContext, Future}

class BookService(client: ElasticClient)(
  implicit config: Config,
  ec: ExecutionContext){

  private def bookRepository: BookRepository = new BookRepository(client)

  def getAll(): Future[Seq[Book]] =
    bookRepository.search(
      bookRepository.search(
        bookRepository.indexName
      )
    )

  def searchBookByID(id: String): Future[Option[Book]] = bookRepository.find(id)

  def searchBooksByAuthor(author: String): Future[Seq[Book]] =
    bookRepository.search(
      bookRepository.search(
        bookRepository.indexName
      ).query(
        TermQuery(
          field = "author.keyword",
          value = author
        )
      )
    )

  def createBook(book: Book): Future[Book] = bookRepository.upsert(book.id, book)

  def updateBook(newBook: Book): Future[Book] = bookRepository.upsert(newBook.id, newBook)

  def deleteByID(id: String) = bookRepository.deleteById(id)
}