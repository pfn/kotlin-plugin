package kotlinplugin

import sbt._

/**
 * @author pfnguyen
 */
object Keys {
  sealed trait KotlinCompileOrder
  object KotlinCompileOrder {
    case object KotlinBefore extends KotlinCompileOrder
    case object KotlinAfter extends KotlinCompileOrder
  }

  val kotlinCompileBefore = TaskKey[Unit]("kotlin-compile-before",
    "runs kotlin compilation before normal compilation if configured to do so")
  val kotlinCompileOrder = SettingKey[KotlinCompileOrder]("kotlin-compile-order",
    "order of kotlin compilation, before or after normal compilation")
  val kotlinSource = SettingKey[File]("kotlin-source", "kotlin source directory")
  val kotlinVersion = SettingKey[String]("kotlin-version",
    "version of kotlin to use for building")
  val kotlincOptions = TaskKey[Seq[String]]("kotlinc-options",
    "options to pass to the kotlin compiler")
  val autoKotlinLibrary = SettingKey[Boolean]("auto-kotlin-library",
    "automatically include kotlin runtime libraries")

  def kotlinLib(name: String) = Def.setting {
    "org.jetbrains.kotlin" % ("kotlin-" + name) % kotlinVersion.value
  }
}
