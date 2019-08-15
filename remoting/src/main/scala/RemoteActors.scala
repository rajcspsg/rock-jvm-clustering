import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object RemoteActors extends App {

  val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("remoteActor.conf"))
  val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("remoteActor.conf").getConfig("remoteSystem"))

  val localSimpleActor = localSystem.actorOf(Props[SimpleActor], "localSimpleActor")
  val remoteSimpleActor = remoteSystem.actorOf(Props[SimpleActor], "remoteSimpleActor")

  localSimpleActor ! "hello local Actor"
  remoteSimpleActor ! "hello remote Actor"
}
