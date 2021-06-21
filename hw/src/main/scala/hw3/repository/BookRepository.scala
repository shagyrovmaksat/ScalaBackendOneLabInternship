package hw3.repository

import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import hw3.domain.Book
import hw3.elastic.ElasticRepository
import hw3.serializer.Serializer

import scala.concurrent.ExecutionContext

class BookRepository(val client: ElasticClient)
                    (implicit val ec: ExecutionContext, config: Config) extends ElasticRepository[Book] with Serializer {
  def indexName: String = config.getString("elastic.indexes.books")

  override implicit val manifest: Manifest[Book] = Manifest.classType[Book](classOf[Book])
}
