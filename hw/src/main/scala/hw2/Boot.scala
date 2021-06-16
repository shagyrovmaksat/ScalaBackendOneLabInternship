package hw2

import akka.actor.{ActorSystem, Props}
import hw2.actors.{Pinger, Ponger}

object Boot extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("Example")
  val userActor = actorSystem.actorSelection("user")

  val ponger = actorSystem.actorOf(Ponger.props("pinger"), "ponger")
  var pinger = actorSystem.actorOf(Pinger.props("ponger"), "pinger")

  pinger ! Pinger.Start("Ping Pong")

  Thread.sleep(100)
  actorSystem.terminate()
}