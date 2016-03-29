kotlinLib("stdlib")

val listClasses = taskKey[Unit]("listClasses")

listClasses := {
  val classes = (classDirectory in Compile).value.***.get
  streams.value.log.info("classes: " + classes)
}
