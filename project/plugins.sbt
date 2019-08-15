resolvers ++= Seq(
  Resolver.sbtPluginRepo("releases"),
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  "Sonatype repository" at "https://oss.sonatype.org/content/repositories/snapshots"
)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)