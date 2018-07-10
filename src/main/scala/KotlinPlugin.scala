package kotlin

import Keys._
import com.hanhuy.sbt.bintray.UpdateChecker
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

  override def projectSettings = Seq(
    libraryDependencies += {
      "org.jetbrains.kotlin" % "kotlin-compiler-embeddable" % kotlinVersion.value % KotlinInternal.name
    },
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
    kotlinVersion := "1.1.4-3",
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
    kotlincOptions := (kotlincOptions in This).value,
    kotlincPluginOptions := (kotlincPluginOptions in This).value,
    kotlinCompile := Def.task {
        KotlinCompile.compile(kotlincOptions.value,
          sourceDirectories.value, kotlincPluginOptions.value,
          dependencyClasspath.value, (managedClasspath in KotlinInternal).value,
          classDirectory.value, streams.value)
    }.dependsOn (compileInputs in (Compile,compile)).value,
    compile := (compile dependsOn kotlinCompile).value,
    kotlinSource := sourceDirectory.value / "kotlin",
    definedTests in Test ++= KotlinTest.kotlinTests.value
  )
}
