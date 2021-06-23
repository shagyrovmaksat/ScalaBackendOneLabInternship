package project.repository

import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import project.domain.HistoryItem
import project.serializer.Serializer

import scala.concurrent.ExecutionContext

class HistoryRepository(val client: ElasticClient)
                    (implicit val ec: ExecutionContext, config: Config) extends ElasticRepository[HistoryItem] with Serializer {

  def indexName: String = config.getString("elastic.indexes.histories")

  override implicit val manifest: Manifest[HistoryItem] = Manifest.classType[HistoryItem](classOf[HistoryItem])
}