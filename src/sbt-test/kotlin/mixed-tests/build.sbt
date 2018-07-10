import sbt.complete.Parsers.spaceDelimited

import scala.xml.{NodeSeq, XML}

kotlinLib("stdlib")

libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.11" % Test
)

lazy val checkTestPass = inputKey[Unit]("Check if a given test-report has one success test")
checkTestPass := {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  val testName = args.head

  val xml = XML.load(s"target/test-reports/$testName.xml")
  val totalTests = getInt(xml \\ "testsuite" \ "@tests")
  val failures = getInt(xml \\ "testsuite" \ "@failures")
  val errors = getInt(xml \\ "testsuite" \ "@errors")
  val skipped = getInt(xml \\ "testsuite" \ "@skipped")

  if (totalTests == 0 || failures > 0 || errors > 0 || skipped > 0) {
    sys.error("Tests not passed")
  }
}

def getInt(path: NodeSeq): Int = path.text.toInt