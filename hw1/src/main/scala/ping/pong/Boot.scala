package ping.pong

import akka.actor.{ActorSystem, Props}
import ping.pong.actors.{Ping, Pong}

object Boot extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("Example")

  var pong = actorSystem.actorOf(Pong.props)
  var ping = actorSystem.actorOf(Props(new Ping(pong)))

  ping ! "Start"
}