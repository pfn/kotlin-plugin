TaskKey[Unit]("check-classes") := {
  val classes = (Compile / classDirectory).value
  val classList = (classes ** "*.class").get
  if (classList.size != 7) {
    throw new MessageOnlyException(s"Incorrect number of classes: ${classList.size} =>\n${classList.mkString("\n")}")
  }
}
