import sbt.Keys._
import sbt._
import sbtassembly._
import AssemblyKeys.{assembly, assemblyMergeStrategy}

object CommonSettings {

  lazy val commonSettings = Seq(
    organization := "fpmortals",
    version := "0.0.1",
    scalaVersion := Version.scalaVer,
    scalacOptions ++= CompileOptions.compileOptions,
    unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value, (javaSource in Compile).value),
    unmanagedSourceDirectories in Test := List((scalaSource in Test).value, (javaSource in Test).value),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    logBuffered in Test := false,
    parallelExecution in Test := false,
    parallelExecution in GlobalScope := false,
    parallelExecution in ThisBuild := false,
    fork in Test := false,
    test in assembly := {},
    libraryDependencies ++= Dependencies.dependencies
  )


  lazy val configure: Project => Project = (proj: Project) => {
    proj
      .settings(CommonSettings.commonSettings: _*)
      .settings(
        assemblyMergeStrategy in assembly := {
          case x =>
            val oldStrategy = (assemblyMergeStrategy in assembly).value
            oldStrategy(x)
        }
      )
  }
}