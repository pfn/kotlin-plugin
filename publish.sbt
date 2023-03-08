ThisBuild / organization := "community.flock.sbt"
ThisBuild / organizationName := "sbt-kotlin-plugin"
ThisBuild / organizationHomepage := Some(url("https://flock.community"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/flock-community/kotlin-plugin"),
    "scm:git@github.com:flock-community/kotlin-plugin.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "Veelenturf",
    name = "Willem Veelenturf",
    email = "willem.veelenturf@flock.community",
    url = url("https://flock.community")
  )
)

ThisBuild / description := "Sbt Kotlin plugin"
ThisBuild / licenses := List(
  "Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")
)
ThisBuild / homepage := Some(url("https://github.com/flock-community/kotlin-plugin"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  // For accounts created after Feb 2021:
  // val nexus = "https://s01.oss.sonatype.org/"
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true