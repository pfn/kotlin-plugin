package kotlinplugin

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

  override def projectSettings = Seq(
    kotlinVersion := BuildInfo.kotlinVersion,
    libraryDependencies <+= kotlinLib("stdlib")
  ) ++ inConfig(Compile)(kotlinSettings) ++
    inConfig(Test)(kotlinSettings)

  val autoImport = Keys

  private[this] val kotlinSettings = List(
    sourceDirectories += kotlinSource.value,
    kotlinCompileOrder := KotlinCompileOrder.KotlinAfter,
    kotlinVersion <<= kotlinVersion in This,
    kotlincOptions := Nil,
    kotlinCompileBefore := {
      if (kotlinCompileOrder.value == KotlinCompileOrder.KotlinBefore) {
        KotlinCompile.compile(kotlincOptions.value, kotlinSource.value,
          sourceDirectories.value, dependencyClasspath.value,
          classDirectory.value, streams.value)
      }
    },
    compile <<= compile dependsOn kotlinCompileBefore,
    compile := {
      if (kotlinCompileOrder.value == KotlinCompileOrder.KotlinAfter) {
        KotlinCompile.compile(kotlincOptions.value, kotlinSource.value,
          sourceDirectories.value, dependencyClasspath.value,
          classDirectory.value, streams.value)
      }
      val analysis = compile.value
      analysis
    },
    kotlinSource := sourceDirectory.value / "kotlin"
  )
}
