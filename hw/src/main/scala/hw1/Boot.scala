package hw1

import akka.actor.{ActorSystem, Props}
import hw1.actors.{Ping, Pong}

object Boot extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("Example")

  var pong = actorSystem.actorOf(Pong.props)
  var ping = actorSystem.actorOf(Props(new Ping(pong)))

  ping ! "Start"
}