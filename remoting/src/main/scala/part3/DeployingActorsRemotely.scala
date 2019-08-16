package part3

import actors.SimpleActor
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object LocalApp extends App  {
  val localSystem = ActorSystem("LocalActorSystem", ConfigFactory.load("DeployingActorsRemotely.conf").getConfig("localApp"))
  val simpleActor = localSystem.actorOf(Props[SimpleActor], "remoteActor")
  simpleActor ! "hello, remote Actor"
}

object RemoteApp extends App  {
  val localSystem = ActorSystem("RemoteActorSystem", ConfigFactory.load("DeployingActorsRemotely.conf").getConfig("remoteApp"))
}
