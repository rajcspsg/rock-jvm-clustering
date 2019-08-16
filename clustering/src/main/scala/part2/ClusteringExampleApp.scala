package part2

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import part2.ClusteringExampleDomain.{ProcessFile, ProcessLine}

import scala.util.Random

case class WorkerObj(address: Address, ref: ActorRef)
object ClusteringExampleDomain {
  case class ProcessFile(filename: String)
  case class ProcessLine(line: String)
  case class ProcessLineResult(count: Int)
}

class Master extends Actor with ActorLogging {

  import context.dispatcher
  implicit val timeout = Timeout(2 seconds)
  val cluster = Cluster(context.system)
  var workers: Map[Address, ActorRef] = Map.empty
  var pendingRemoval: Map[Address, ActorRef] = Map.empty


  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = handleMemeberEvents orElse handleMemberRegistration orElse handleJob

  def handleMemeberEvents: Receive = {
    case MemberUp(member) if member.hasRole("worker") =>
      if(pendingRemoval.contains(member.address)) {
        pendingRemoval = pendingRemoval - member.address
      } else {
        log.info(s"New memeber to the cluster ${member.address}")
        val workerSelection = context.actorSelection(s"${member.address}/user/worker")
        workerSelection.resolveOne().map(ref => WorkerObj(member.address,ref)).pipeTo(self)
      }


    case UnreachableMember(member)  if member.hasRole("worker") =>
      log.info(s"oh no, ${member.address} is not reachable")
      val workerOption = workers.get(member.address)
      workerOption.foreach { ref => {
        pendingRemoval = pendingRemoval + (member.address -> ref)
      }}
    case MemberRemoved(member, previousStatus) =>
      log.info(s"$member is removed after state $previousStatus")
      workers = workers - member.address

    case m: MemberEvent =>
  }

  def handleMemberRegistration: Receive = {
    case pair:WorkerObj =>
      log.info(s"registering worker $pair")
      workers = workers + (pair.address -> pair.ref)
  }

  def handleJob: Receive = {
    case ProcessFile(filename) =>
      scala.io.Source.fromResource(filename).getLines().foreach { line =>
        var workerIndex = Random.nextInt((workers -- pendingRemoval.keys).size)
        val worker: ActorRef = (workers -- pendingRemoval.keys).values.toSeq(workerIndex)
        worker ! ProcessLine(line)
      }
  }

}

class Worker extends Actor with ActorLogging {

  override def receive: Receive = {
    case _ => //TODO
  }
}

object SeeNodeApp extends App {
  def createNode(port: Int, role: String, props: Props, actorName: String): Unit = {
    val config = ConfigFactory.parseString(
      s"""
         |akka.cluster.roles = ["$role"]
         |akka.remote.artery.canonical.port = $port
       """.stripMargin
    )
        .withFallback(ConfigFactory.load("clusteringExample.conf"))

    val system = ActorSystem("RTJVMCluster", config)
    system.actorOf(props, actorName)
  }

  createNode(2551, "master", Props[Master], "master")
  createNode(2552, "worker", Props[Worker], "worker-1")
  createNode(2553, "worker", Props[Worker], "worker-2")
}

object ClusteringExampleApp extends App {


}
