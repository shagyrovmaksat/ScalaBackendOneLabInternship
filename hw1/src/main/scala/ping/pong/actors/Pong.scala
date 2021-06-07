package ping.pong.actors

import akka.actor.{Actor, ActorLogging, Props}

object Pong {
  def props: Props = Props(new Pong)
}

class Pong extends Actor with ActorLogging {

  override def receive: Receive = {
    case a@"Ping" =>
      log.info(s"$a")
      sender ! "Pong"

    case a@"Stop" =>
      log.info(s"$a")
      context.stop(self)
  }
}