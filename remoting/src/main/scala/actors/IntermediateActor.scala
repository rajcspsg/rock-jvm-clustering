package actors

import akka.actor.{Actor, ActorRef}

class IntermediateActor extends Actor {

  override def receive: Receive = {
    case (m: Any, ref: ActorRef) => {
      println( s"sender ${sender()}\t self ${self}")
      ref.tell(m, self)
    }
  }

}
