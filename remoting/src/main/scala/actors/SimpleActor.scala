package actors

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout

import scala.concurrent.duration._

class SimpleActor extends  Actor with ActorLogging {

  implicit  val timeout = Timeout(2 seconds)

  override def preStart(): Unit = {
    //val me = self ? Identify
    //println(s"Im $me")
  }

  override def receive: Receive = {

    case m => {
      val sendBy = sender()
      println(s"received $m from $sendBy")
    }
  }

}
