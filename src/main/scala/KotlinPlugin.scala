package kotlin

import Keys._
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

/**
 * @author pfnguyen
 */
object KotlinPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectConfigurations = KotlinInternal :: Nil

  override def globalSettings = (onLoad := onLoad.value andThen { s =>
    Project.runTask(updateCheck in Keys.Kotlin, s).fold(s)(_._1)
  }) :: Nil

  private def kotlinScriptCompilerDeps(kotlinVer: String) = {
    import scala.math.Ordering.Implicits.infixOrderingOps

    if (KotlinVersion(kotlinVer) <= KotlinVersion("1.3.21")) {
      Seq(
        "org.jetbrains.kotlin" % "kotlin-script-runtime" % kotlinVer
      )
    } else {
      Seq(
        "org.jetbrains.kotlin" % "kotlin-scripting-compiler-embeddable" % kotlinVer % KotlinInternal.name,
        "org.jetbrains.kotlin" % "kotlin-scripting-compiler-embeddable" % kotlinVer
      )
    }
  }

  override def projectSettings = Seq(
    libraryDependencies ++= Seq(
      "org.jetbrains.kotlin" % "kotlin-compiler-embeddable" % kotlinVersion.value % KotlinInternal.name
    ) ++ kotlinScriptCompilerDeps(kotlinVersion.value),
    managedClasspath in KotlinInternal := Classpaths.managedJars(KotlinInternal, classpathTypes.value, update.value),
    updateCheck in Kotlin := {
      val log = streams.value.log
      UpdateChecker("pfn", "sbt-plugins", "kotlin-plugin") {
        case Left(t) =>
          log.debug("Failed to load version info: " + t)
        case Right((versions, current)) =>
          log.debug("available versions: " + versions)
          log.debug("current version: " + BuildInfo.version)
          log.debug("latest version: " + current)
          if (versions(BuildInfo.version)) {
            if (BuildInfo.version != current) {
              log.warn(
                s"UPDATE: A newer kotlin-plugin is available:" +
                  s" $current, currently running: ${BuildInfo.version}")
            }
          }
      }
    },
    kotlinVersion := "1.3.50",
    kotlincJvmTarget := "1.8",
    kotlincOptions := Nil,
    kotlincPluginOptions := Nil,
    watchSources     ++= {
      import language.postfixOps
      val kotlinSources = "*.kt" || "*.kts"
      (sourceDirectories in Compile).value.flatMap(_ ** kotlinSources get) ++
        (sourceDirectories in Test).value.flatMap(_ ** kotlinSources get)
    }
  ) ++ inConfig(Compile)(kotlinCompileSettings) ++
    inConfig(Test)(kotlinCompileSettings)

  val autoImport = Keys

  // public to allow kotlin compile in other configs beyond Compile and Test
  val kotlinCompileSettings = List(
    unmanagedSourceDirectories += kotlinSource.value,
    kotlincOptions := kotlincOptions.value,
    kotlincJvmTarget := kotlincJvmTarget.value,
    kotlincPluginOptions := kotlincPluginOptions.value,
    kotlinCompile := Def.task {
        KotlinCompile.compile(kotlincOptions.value,
          kotlincJvmTarget.value,
          kotlinVersion.value,
          sourceDirectories.value, kotlincPluginOptions.value,
          dependencyClasspath.value, (managedClasspath in KotlinInternal).value,
          classDirectory.value, streams.value)
    }.dependsOn (compileInputs in (Compile,compile)).value,
    compile := (compile dependsOn kotlinCompile).value,
    kotlinSource := sourceDirectory.value / "kotlin",
    definedTests in Test ++= KotlinTest.kotlinTests.value
  )
}
