package sbt

import sbt.Keys._
import sbt.classfile.Analyze
import sbt.classpath.ClasspathUtilities
import sbt.inc.{Analysis, IncOptions, IncrementalCompile}
import xsbti.compile.SingleOutput

object KotlinTest {
  val kotlinTests = Def.task {
    val old = (definedTests in Test).value
    val out = ((target in Test).value ** "scala-*").get.head / "test-classes"
    val srcs = ((sourceDirectory  in Test).value ** "*.kt").get.toList
    val xs = (out ** "*.class").get.toList

    val loader = ClasspathUtilities.toLoader((fullClasspath in Test).value map {
      _.data
    })
    val log = sLog.value
    val a0 = IncrementalCompile(
      srcs.toSet, s => None,
      (fs, changs, callback) => {
        def readAPI(source: File, classes: Seq[Class[_]]): Set[String] = {
          val (api, inherits) = ClassToAPI.process(classes)
          callback.api(source, api)
          inherits.map(_.getName)
        }

        Analyze(xs, srcs, log)(callback, loader, readAPI)
      },
      Analysis.Empty, f => None,
      new SingleOutput {
        def outputDirectory = out
      },
      log,
      IncOptions.Default)._2
    val frameworks = (loadedTestFrameworks in Test).value.values.toList
    log.info(s"Compiling ${srcs.length} Kotlin source to ${out}...")
    old ++ Tests.discover(frameworks, a0, log)._1
  }
}
