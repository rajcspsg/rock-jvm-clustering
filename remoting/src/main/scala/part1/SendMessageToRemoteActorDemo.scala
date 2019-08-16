package part1

import actors.{ActorResolver, SimpleActor}
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

object SendMessageToRemoteActorDemo extends App {

  val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("remoteActor.conf"))
  val localSimpleActor = localSystem.actorOf(Props[SimpleActor], "localSimpleActor")
  val remoteActorSelection = localSystem.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActor")
  remoteActorSelection ! "hello remote Actor"

  import localSystem.dispatcher
  import scala.concurrent.duration._
  implicit val timeout = Timeout(2 seconds)
  remoteActorSelection.resolveOne().onComplete {
    case Success(actorRef) => actorRef ! "I've resolved akka://RemoteSystem@localhost:2552/user/remoteSimpleActor"
    case Failure(exception) => println(s"exception happened $exception")
  }


  val localActorResolver = localSystem.actorOf(Props[ActorResolver], "localActorResolver")

}

object StartRemoteActorApp extends App {
  val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("remoteActor.conf").getConfig("remoteSystem"))
  val remoteSimpleActor = remoteSystem.actorOf(Props[SimpleActor], "remoteSimpleActor")
}
