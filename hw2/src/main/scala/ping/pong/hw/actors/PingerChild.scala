package ping.pong.hw.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object PingerChild {

  def props(ponger: ActorRef): Props = Props(new PingerChild(ponger))

}

class PingerChild(ponger: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case clazz@Pong(message) =>
      log.info(s"Hi! I'm Pinger child. Class: ${clazz.getClass.getSimpleName}, value: $message, sender: $sender")
      ponger ! Ping(message)
  }
}