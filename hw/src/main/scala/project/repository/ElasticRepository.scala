package project.repository

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, Indexable, Response}
import com.sksamuel.elastic4s.akka.{AkkaHttpClient, AkkaHttpClientSettings}
import com.sksamuel.elastic4s.requests.delete.DeleteByQueryResponse
import com.sksamuel.elastic4s.requests.searches.{SearchRequest, SearchResponse}
import com.typesafe.config.Config
import org.json4s.Formats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}

object ElasticClientBuilder {

  def create()(implicit config: Config, system: ActorSystem): ElasticClient = {
    val hosts: Seq[String] = config.getString("elastic.addresses").split(",").map(_.trim).toSeq
    ElasticClient(AkkaHttpClient(AkkaHttpClientSettings(config.getConfig("elastic")).copy(hosts = hosts.toVector)))
  }

}

trait ElasticRepository[T <: AnyRef] extends ElasticDsl {

  def indexName: String
  def client: ElasticClient
  implicit val ec: ExecutionContext
  implicit val formats: Formats
  implicit val manifest: Manifest[T]
  implicit object EntityIndexable extends Indexable[T] {
    override def json(entity: T): String = write(entity)
  }

  def find(id: String): Future[Option[T]] = {
    val response: Future[Response[SearchResponse]] = client.execute {
      search(s"$indexName")
        .query(idsQuery(id))
    }

    response.map { r =>
      if (r.isSuccess && r.result.hits.total.value > 0) {
        Some(parse(r.result.hits.hits.head.sourceAsString).extract[T])
      } else {
        None
      }
    }
  }

  def search(searchRequest: SearchRequest): Future[Seq[T]] =
    client.execute(searchRequest).map { response =>
      val r = response.result.hits.hits.map(_.sourceAsString)
      val result = scala.collection.mutable.ListBuffer.empty[T]
      r.foreach { string =>
        val t: T = parse(string).extract[T]
        result += t
      }
      result
    }

  def deleteById(id: String): Future[Response[DeleteByQueryResponse]] =
    client.execute {
      deleteByQuery(index = s"$indexName", termQuery(field = "id.keyword", value = id))
    }

  def upsert(id: String, entity: T): Future[T] =
    client.execute(
      indexInto(s"$indexName")
        .withId(id)
        .refreshImmediately
        .doc(entity)
    ).map { r =>
      if (r.isSuccess) {
        entity
      } else {
        throw r.error.asException
      }
    }
}

