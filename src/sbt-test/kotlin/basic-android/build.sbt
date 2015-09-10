kotlincOptions in Compile <++= Def.task {
  "-cp" :: (bootClasspath in Android).value.map(_.data.getAbsolutePath).mkString(java.io.File.pathSeparator) ::
  Nil
}

libraryDependencies <+= kotlinLib("stdlib")
