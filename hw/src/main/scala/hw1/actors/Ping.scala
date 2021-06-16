package hw1.actors

import akka.actor.{Actor, ActorLogging, ActorRef}

class Ping(pong: ActorRef) extends Actor with ActorLogging {
  var count = 0
  def increment = count += 1

  override def receive: Receive = {
    case a@"Start" =>
      log.info(s"$a count = $count")

      increment
      pong ! "Ping"

    case a@"Pong" =>
      log.info(s"$a count = $count")

      increment
      if (count >= 100) {
        sender ! "Stop"
        context.stop(self)
      } else {
        sender ! "Ping"
      }
  }
}