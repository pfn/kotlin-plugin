TaskKey[Unit]("check-classes") := {
  val classes = (classDirectory in Compile).value
  val classList = (classes ** "*.class").get
  if (classList.size != 6) {
    throw new MessageOnlyException(s"Incorrect number of classes: ${classList.size} =>\n${classList.mkString("\n")}")
  }
}
