//import ScriptedPlugin._
import bintray.Keys._

val kotlinVersion = "1.0.0-beta-4589"

def kotlinLib(name: String) =
  "org.jetbrains.kotlin" % ("kotlin-" + name) % kotlinVersion

name := "kotlin-plugin"

organization := "com.hanhuy.sbt"

version := "0.9.3-SNAPSHOT"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

libraryDependencies ++=
  "com.hanhuy.sbt" %% "bintray-update-checker" % "0.1" ::
  kotlinLib("compiler-embeddable") ::
  Nil

sbtPlugin := true

// build info plugin

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq(version, ("kotlinVersion", kotlinVersion))

buildInfoPackage := "kotlinplugin"

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
