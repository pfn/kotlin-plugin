kotlinLib("stdlib")

kotlinVersion := "1.1.4-3"

val listClasses = taskKey[Unit]("listClasses")

listClasses := {
  val classes = (classDirectory in Compile).value.listFiles()
  streams.value.log.info("classes: " + classes)
}
