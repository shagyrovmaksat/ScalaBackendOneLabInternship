package project

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.{Config, ConfigFactory}
import project.actors.Handler
import project.repository.ElasticClientBuilder
import project.route.Routes

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContextExecutor

object Boot extends App with Routes {

  implicit val config: Config = ConfigFactory.load()
  val systemName = config.getString("akka.system")

  implicit val timeout: Timeout                           = Timeout(60, TimeUnit.SECONDS)
  implicit val actorSystem: ActorSystem                   = ActorSystem(systemName)
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  val host: String = config.getString("akka.http.host")
  val port: Int = config.getInt("akka.http.port")

  val elasticClient: ElasticClient = ElasticClientBuilder.create()

  def handlerProps: Props = Handler.props(elasticClient)

  Http().bindAndHandle(routes, host, port)
}
