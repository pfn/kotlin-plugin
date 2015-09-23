kotlincPluginOptions in Compile ++= {
  val plugin = KotlinPluginOptions("org.jetbrains.kotlin.android")
  val layout = (projectLayout in Android).value
  plugin.option("androidRes", layout.res.getAbsolutePath) ::
  plugin.option("androidManifest", layout.manifest.getAbsolutePath) ::
    Nil
}
kotlinClasspath(Compile, bootClasspath in Android)

libraryDependencies <+= kotlinPlugin("android-extensions")

libraryDependencies <+= kotlinLib("stdlib")
