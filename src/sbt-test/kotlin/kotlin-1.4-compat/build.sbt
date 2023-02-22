kotlinLib("stdlib")

kotlinVersion := "1.4.32"

val listClasses = taskKey[Unit]("listClasses")

listClasses := {
  val classes = (Compile / classDirectory).value.listFiles()
  streams.value.log.info("classes: " + classes)
}
