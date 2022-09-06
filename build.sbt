name := "kotlin-plugin"

organization := "com.hanhuy.sbt"

version := "2.0.1-SNAPSHOT"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")
/*
libraryDependencies ++= Seq(
  "com.hanhuy.sbt" %% "bintray-update-checker" % "0.2"
)
*/

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.2",
  "org.scalaz" %% "scalaz-core" % "7.2.28"
)

sbtPlugin := true

// build info plugin

enablePlugins(BuildInfoPlugin, SbtPlugin)

buildInfoPackage := "kotlin"

// bintray
bintrayRepository := "sbt-plugins"

publishMavenStyle := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization := None

// scripted
scriptedLaunchOpts ++= Seq(
  "-Xmx1024m",
  "-Dplugin.org=" + organization.value,
  "-Dplugin.name=" + name.value,
  "-Dplugin.version=" + version.value)
