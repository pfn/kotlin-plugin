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
    kotlincOptions := Nil,
    kotlincPluginOptions := Nil,
    watchSources     <++= Def.task {
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
    sourceDirectories += kotlinSource.value,
    kotlincOptions <<= kotlincOptions in This,
    kotlincPluginOptions <<= kotlincPluginOptions in This,
    kotlinCompileBefore <<= Def.task {
        KotlinCompile.compile(kotlincOptions.value,
          sourceDirectories.value, kotlincPluginOptions.value,
          dependencyClasspath.value, classDirectory.value, streams.value)
    } dependsOn (compileInputs in (Compile,compile)),
    compile <<= compile dependsOn kotlinCompileBefore,
    kotlinSource := sourceDirectory.value / "kotlin"
  )
}
