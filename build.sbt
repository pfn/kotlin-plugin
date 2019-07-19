//import ScriptedPlugin._
import bintray.Keys._

name := "kotlin-plugin"

organization := "com.hanhuy.sbt"

version := "1.0.10-SNAPSHOT"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

libraryDependencies ++=
  "com.hanhuy.sbt" %% "bintray-update-checker" % "0.2" ::
  Nil

sbtPlugin := true

// build info plugin

buildInfoSettings

sourceGenerators in Compile += buildInfo

buildInfoPackage := "kotlin"

// bintray
bintrayPublishSettings

repository in bintray := "sbt-plugins"

publishMavenStyle := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization in bintray := None

// scripted
scriptedSettings

scriptedLaunchOpts ++= "-Xmx1024m" ::
  "-Dplugin.version=" + version.value ::
  Nil
