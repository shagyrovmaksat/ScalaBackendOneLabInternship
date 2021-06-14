package ping.pong.hw

import akka.actor.{ActorSystem, Props}
import ping.pong.hw.actors.{Ping, Pinger, Ponger}

object Boot extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("Example")
  val userActor = actorSystem.actorSelection("user")

  val ponger = actorSystem.actorOf(Ponger.props(actorSystem), "parent2")
  val childPonger = actorSystem.actorSelection("user/parent2/child2")

  var pinger = actorSystem.actorOf(Pinger.props(ponger, childPonger), "parent1")

  pinger ! Pinger.Start(Ping("Ping Pong"))

  Thread.sleep(100)
  actorSystem.terminate()
}