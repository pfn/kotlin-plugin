package kotlinplugin

import sbt._

/**
 * @author pfnguyen
 */
object Keys {
  sealed trait KotlinCompileOrder

  val Kotlin = config("kotlin")

  val updateCheck = TaskKey[Unit]("update-check", "check for a new version of the plugin")
  val kotlinCompileBefore = TaskKey[Unit]("kotlin-compile-before",
    "runs kotlin compilation before normal compilation if configured to do so")
  val kotlincPluginOptions = TaskKey[Seq[String]]("kotlinc-plugin-options",
    "kotlin compiler plugin options")
  val kotlinSource = SettingKey[File]("kotlin-source", "kotlin source directory")
  val kotlinVersion = SettingKey[String]("kotlin-version",
    "version of kotlin to use for building")
  val kotlincOptions = TaskKey[Seq[String]]("kotlinc-options",
    "options to pass to the kotlin compiler")

  def kotlinLib(name: String) = Def.setting {
    "org.jetbrains.kotlin" % ("kotlin-" + name) % kotlinVersion.value
  }

  def kotlinPlugin(name: String) = kotlinLib(name)(_ % "provided")

  def kotlinClasspath(config: Configuration, classpathKey: TaskKey[sbt.Keys.Classpath]): Setting[_] =
    kotlincOptions in config <++= Def.task {
    "-cp" :: classpathKey.value.map(_.data.getAbsolutePath).mkString(
      java.io.File.pathSeparator) ::
      Nil
  }

  case class KotlinPluginOptions(pluginId: String) {
    def option(key: String, value: String) =
      s"plugin:$pluginId:$key=$value"
  }
}
