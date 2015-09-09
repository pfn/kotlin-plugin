//import ScriptedPlugin._
import bintray.Keys._

name := "kotlin-plugin"

organization := "com.hanhuy.sbt"

version := "0.1-SNAPSHOT"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

libraryDependencies ++= Seq(
)

sbtPlugin := true

// bintray
bintrayPublishSettings

repository in bintray := "sbt-plugins"

publishMavenStyle := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization in bintray := None
