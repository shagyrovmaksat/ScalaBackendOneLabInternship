package ping.pong.hw.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props}

object PongerChild {

  def props(actorSystem: ActorSystem): Props = Props(new PongerChild(actorSystem))

}

class PongerChild(actorSystem: ActorSystem) extends Actor with ActorLogging {

  override def receive: Receive = {
    case clazz@Ping(message) =>
      log.info(s"Hi! I'm Ponger child. Class: ${clazz.getClass.getSimpleName}, value: $message, sender: $sender")
      val child1 = actorSystem.actorSelection("user/parent1/child1")
      child1 ! Pong(message)
  }

}