package hw2.actors

import akka.actor.{Actor, ActorLogging, Props}
import hw2.actors.Pinger.{Pong, Start}

object Pinger {

  case class Start(message: String)

  case class Pong(message: String)

  def props(ponger: String): Props = Props(new Pinger(ponger))

}

class Pinger(ponger: String) extends Actor with ActorLogging {

  val childPinger = context.actorOf(Pinger.props(ponger), name = "pingerChild")

  override def receive: Receive = {
    case a@Start(ping) =>
      log.info(s"Hi! I'm Pinger. Class: ${a.getClass.getSimpleName}, value: ${ping}, sender: $sender")
      context.actorSelection(s"/user/$ponger/pongerChild") ! Ponger.Ping(ping)

    case clazz@Pong(message) =>
      log.info(s"Hi! I'm Pinger. Class: ${clazz.getClass.getSimpleName}, value: ${message}, sender: $sender")
      context.actorSelection(s"/user/$ponger/pongerChild") ! Ponger.Ping(message)
  }
}
