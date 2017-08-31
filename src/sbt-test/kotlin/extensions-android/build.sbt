enablePlugins(AndroidApp)
javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

kotlinClasspath(Compile, bootClasspath in Android)
kotlinPlugin("android-extensions")
kotlinLib("stdlib")
kotlincPluginOptions in Compile ++= {
  val plugin = KotlinPluginOptions("org.jetbrains.kotlin.android")
  val layout = (projectLayout in Android).value
  plugin.option("package", applicationId.value) ::
  plugin.option("variant", "main;" + layout.res.getCanonicalPath) ::
    Nil
}
