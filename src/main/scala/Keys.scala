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
  val kotlincPluginOptions = TaskKey[Seq[String]]("kotlinc-plugin-options",
    "kotlin compiler plugin options")
  val kotlinCompileJava = SettingKey[Boolean]("kotlin-compile-java",
    "whether kotlinc should also compile java (to allow for mixed compilation)")
  val kotlinSource = SettingKey[File]("kotlin-source", "kotlin source directory")
  val kotlinVersion = SettingKey[String]("kotlin-version",
    "version of kotlin to use for building")
  val kotlincOptions = TaskKey[Seq[String]]("kotlinc-options",
    "options to pass to the kotlin compiler")

  def kotlinLib(name: String) = Def.setting {
    "org.jetbrains.kotlin" % ("kotlin-" + name) % kotlinVersion.value
  }

  def kotlinPlugin(name: String) = kotlinLib(name)(_ % "provided")

  def kotlinClasspath(classpathKey: TaskKey[sbt.Keys.Classpath]) = Def.task {
    "-cp" :: classpathKey.value.map(_.data.getAbsolutePath).mkString(
      java.io.File.pathSeparator) ::
      Nil
  }

  case class KotlinPluginOptions(pluginId: String) {
    def option(key: String, value: String) =
      s"plugin:$pluginId:$key=$value"
  }
}
