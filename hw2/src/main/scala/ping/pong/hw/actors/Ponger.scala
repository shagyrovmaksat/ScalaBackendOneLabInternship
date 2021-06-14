package ping.pong.hw.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props}

object Ponger {

  def props(actorSystem: ActorSystem): Props = Props(new Ponger(actorSystem))
}

class Ponger(actorSystem: ActorSystem) extends Actor with ActorLogging {

  val childPonger = context.actorOf(PongerChild.props(actorSystem), name = "child2")

  override def receive: Receive = {
    case clazz@Ping(message) =>
      log.info(s"Hi! I'm Ponger. Class: ${clazz.getClass.getSimpleName}, value: $message, sender: $sender")
      val parent1 = actorSystem.actorSelection("user/parent1")
      parent1 ! Pong(message)
  }
}
