package part1

import akka.actor
import akka.actor.{Actor, ActorLogging, ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberJoined, MemberRemoved, MemberUp, UnreachableMember}
import com.typesafe.config.ConfigFactory

class ClusterSubscriber extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info(s"New memeber to the cluster ${member.address}")
    case MemberUp(member) =>
      log.info(s"welcome new memeber to our cluster ${member.address}")
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Poor ${member.address} is no more in our cluster")
    case UnreachableMember(memeber) =>
      log.info(s"oh no, ${memeber.address} is not reachable")
    case m:MemberEvent =>
      log.info(s"another member event $m")
  }
}

object ClusteringBasicsApp extends App {

  def startCluster(ports: List[Int]): Unit = {
    ports.foreach { port =>
     val config = ConfigFactory.parseString(
        s"""
          |akka.remote.artery.canonical.port = $port
        """.stripMargin
      ).withFallback(ConfigFactory.load("basics.conf"))
      val clusterSystem = ActorSystem("RTJMCluster", config)
      clusterSystem.actorOf(Props[ClusterSubscriber], "clusterSubscriber")
    }
  }
startCluster(List(2551, 2552, 0))

}

object CLusterManualRegApp extends App {

  val config = ConfigFactory.load("basics.conf").getConfig("manualRegistration")
  val system =  ActorSystem("RTJMCluster", config)
  val cluster = Cluster(system)
  cluster.joinSeedNodes(List(Address("akka", "RTJMCluster", "localhost", 2551), Address("akka", "RTJMCluster", "localhost", 2552)))
  system.actorOf(Props[ClusterSubscriber], "clusterSubscriber")
}
