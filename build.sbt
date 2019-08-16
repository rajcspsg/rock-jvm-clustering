name := "rock-jvm-clustering"

version := "0.1"

scalaVersion := "2.13.0"

lazy val `rock-jvm-clustering` = (project in file(".")).aggregate(remoting)

lazy val remoting = project.settings(CommonSettings.commonSettings: _*)
lazy val clustering = project.settings(CommonSettings.commonSettings: _*)