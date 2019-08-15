import sbt._

object Version {
  val logbackVer        = "1.2.3"
  val scalaVer          = "2.12.6"
  val scalaTestVer      = "3.0.4"
  lazy val akkaVersion = "2.5.24"
  lazy val protobufVersion = "3.6.1"
}



object Dependencies {
  val dependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % Version.akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % Version.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % Version.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % Version.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % Version.akkaVersion,
    "ch.qos.logback"                 %  "logback-classic"             % Version.logbackVer,
    "ch.qos.logback"                 %  "logback-classic"             % Version.logbackVer,
    "org.scalatest"                 %% "scalatest"                    % Version.scalaTestVer         % Test
  )
}