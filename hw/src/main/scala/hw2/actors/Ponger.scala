package hw2.actors

import akka.actor.{Actor, ActorLogging, Props}
import hw2.actors.Ponger.Ping

object Ponger {

  case class Ping(message: String)

  def props(pinger: String): Props = Props(new Ponger(pinger))

}

class Ponger(pinger: String) extends Actor with ActorLogging {

  val childPonger = context.actorOf(Ponger.props(pinger), name = "pongerChild")

  override def receive: Receive = {
    case clazz@Ping(message) =>
      log.info(s"Hi! I'm Ponger. Class: ${clazz.getClass.getSimpleName}, value: $message, sender: $sender")
      context.actorSelection(s"/user/$pinger") ! Pinger.Pong(message)
  }
}
