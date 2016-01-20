kotlincPluginOptions in Compile ++= {
  val plugin = KotlinPluginOptions("org.jetbrains.kotlin.android")
  val layout = (projectLayout in Android).value
  plugin.option("package", applicationId.value) ::
  plugin.option("variant", "main;" + layout.res.getCanonicalPath) ::
    Nil
}
kotlinClasspath(Compile, bootClasspath in Android)

libraryDependencies <+= kotlinPlugin("android-extensions")

libraryDependencies <+= kotlinLib("stdlib")
