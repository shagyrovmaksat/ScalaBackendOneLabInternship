package ping.pong.hw.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import ping.pong.hw.actors.Pinger.Start

object Pinger {

  case class Start(message: Ping)

  def props(ponger: ActorRef, childPonger: ActorSelection): Props = Props(new Pinger(ponger, childPonger))

}

class Pinger(ponger: ActorRef, childPonger: ActorSelection) extends Actor with ActorLogging {

  val childPinger = context.actorOf(PingerChild.props(ponger), name = "child1")

  override def receive: Receive = {
    case a@Start(ping) =>
      log.info(s"Hi! I'm Pinger. Class: ${a.getClass.getSimpleName}, value: ${ping.message}, sender: $sender")
      childPonger ! ping

    case clazz@Pong(message) =>
      log.info(s"Hi! I'm Pinger. Class: ${clazz.getClass.getSimpleName}, value: ${message}, sender: $sender")
      childPonger ! Ping(message)
  }
}
