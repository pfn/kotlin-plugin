kotlinLib("stdlib")

kotlinVersion := "1.6.21"

val listClasses = taskKey[Unit]("listClasses")

listClasses := {
  val classes = (Compile / classDirectory).value.listFiles()
  streams.value.log.info("classes: " + classes)
}
