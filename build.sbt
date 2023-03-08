name := "sbt-kotlin-plugin"

organization := "community.flock.sbt"

version := "3.0.1"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.2",
  "org.scalaz" %% "scalaz-core" % "7.2.28"
)

sbtPlugin := true

// build info plugin

enablePlugins(BuildInfoPlugin, SbtPlugin)

buildInfoPackage := "kotlin"

// scripted
scriptedLaunchOpts ++= Seq(
  "-Xmx1024m",
  "-Dplugin.version=" + version.value
)
