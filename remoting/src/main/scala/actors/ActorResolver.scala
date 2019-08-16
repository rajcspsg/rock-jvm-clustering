package actors

import akka.actor.{Actor, ActorIdentity, ActorLogging, Identify}

class ActorResolver extends Actor with ActorLogging {

  override def preStart(): Unit = {
    val selection = context.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActor")
    selection ! Identify(42)
  }
  override def receive: Receive = {
    case ActorIdentity(42, Some(actorRef)) => actorRef ! "Thank you for identifying yourself"
  }

}
