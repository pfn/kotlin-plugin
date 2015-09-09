kotlincPluginOptions in Compile ++= {
  val plugin = KotlinPluginOptions("org.jetbrains.kotlin.android")
  val layout = (projectLayout in Android).value
  plugin.option("androidRes", layout.res.getAbsolutePath) ::
  plugin.option("androidManifest", layout.manifest.getAbsolutePath) ::
  plugin.option("supportV4", file("/").getAbsolutePath) :: // too lazy to actually search, also not included in this test
    Nil
}
kotlincOptions in Compile <++= Def.task {
  "-cp" :: (bootClasspath in Android).value.map(_.data.getAbsolutePath).mkString(java.io.File.pathSeparator) ::
  Nil
}

libraryDependencies <+= kotlinLib("android-extensions")
