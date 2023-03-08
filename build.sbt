name := "sbt-kotlin-plugin"

organization := "community.flock.sbt"

version := "3.0.0-SNAPSHOT"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.2",
  "org.scalaz" %% "scalaz-core" % "7.2.28"
)

sbtPlugin := true

// build info plugin

enablePlugins(BuildInfoPlugin, SbtPlugin)

buildInfoPackage := "kotlin"

publishMavenStyle := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

// scripted
scriptedLaunchOpts ++= Seq(
  "-Xmx1024m",
  "-Dplugin.version=" + version.value
)

//publishTo := Some("Artifactory Realm" at "https://flock.jfrog.io/artifactory/flock-sbt")
//publishMavenStyle := true
//credentials += (if (sys.env.contains("CI")) {
//  Credentials(
//    "Artifactory Realm",
//    "flock.jfrog.io",
//    "github",
//    sys.env("JFROG_TOKEN")
//  )
//} else {
//  Credentials(
//    Path.userHome / ".sbt" / ".credentials"
//  )
//})
