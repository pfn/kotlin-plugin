//import ScriptedPlugin._
import bintray.Keys._

val kotlinVersion = "0.13.1514"

def kotlinLib(name: String) =
  "org.jetbrains.kotlin" % ("kotlin-" + name) % kotlinVersion

name := "kotlin-plugin"

organization := "com.hanhuy.sbt"

version := "0.8"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

libraryDependencies ++=
  kotlinLib("compiler") ::
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
