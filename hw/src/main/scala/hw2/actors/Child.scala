package hw2.actors

import akka.actor.{Actor, ActorLogging, Props}

object Child {

  def props(pinger: String, ponger: String): Props = Props(new Child(pinger, ponger))

}

class Child(pinger: String, ponger: String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case clazz@Pinger.Pong(message) =>
      log.info(s"Hi! I'm Pinger child. Class: ${clazz.getClass.getSimpleName}, value: $message, sender: $sender")
      context.actorSelection(s"/user/$ponger") ! Ponger.Ping(message)

    case clazz@Ponger.Ping(message) =>
      log.info(s"Hi! I'm Pinger child. Class: ${clazz.getClass.getSimpleName}, value: $message, sender: $sender")
      context.actorSelection(s"/user/$pinger/pingerChild") ! Ponger.Ping(message)
  }
}