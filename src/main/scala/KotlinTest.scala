package sbt

import sbt.Keys._
import sbt.internal.inc.classfile.Analyze
import sbt.internal.inc.classpath.ClasspathUtilities
import sbt.internal.inc._
import xsbti.compile._

object KotlinTest {
  private object EmptyLookup extends Lookup {
    def changedClasspathHash: Option[Vector[FileHash]] = None

    def analyses: Vector[CompileAnalysis] = Vector.empty

    def lookupOnClasspath(binaryClassName: String): Option[File] = None

    def lookupAnalysis(binaryClassName: String): Option[CompileAnalysis] = None
    def changedBinaries(previousAnalysis: xsbti.compile.CompileAnalysis): Option[Set[java.io.File]] = None
    def changedSources(previousAnalysis: xsbti.compile.CompileAnalysis): Option[xsbti.compile.Changes[java.io.File]] = None
    def removedProducts(previousAnalysis: xsbti.compile.CompileAnalysis): Option[Set[java.io.File]] = None
    def shouldDoIncrementalCompilation(changedClasses: Set[String],analysis: xsbti.compile.CompileAnalysis): Boolean = true

    def hashClasspath(x$1: Array[java.io.File]): java.util.Optional[Array[xsbti.compile.FileHash]] = java.util.Optional.empty()
  }

  val kotlinTests = Def.task {
    val out = ((target in Test).value ** "scala-*").get.head / "test-classes"
    val srcs = ((sourceDirectory  in Test).value ** "*.kt").get.toList
    val xs = (out ** "*.class").get.toList

    val loader = ClasspathUtilities.toLoader((fullClasspath in Test).value map {
      _.data
    })
    val log = streams.value.log
    val a0 = IncrementalCompile(
      srcs.toSet,
      EmptyLookup,
      (fs, changs, callback, clsFileMgr) => {
        def readAPI(source: File, classes: Seq[Class[_]]): Set[(String, String)] = {
          val (apis, mainClasses, inherits) = ClassToAPI.process(classes)
          apis.foreach(callback.api(source, _))
          mainClasses.foreach(callback.mainClass(source, _))
          inherits.map {
            case (from, to) => (from.getName, to.getName)
          }
        }

        Analyze(xs, srcs, log)(callback, loader, readAPI)
      },
      Analysis.Empty,
      new SingleOutput {
        def getOutputDirectory(): java.io.File = out
      },
      log,
      incOptions.value)._2
    val frameworks = (loadedTestFrameworks in Test).value.values.toList
    log.info(s"Compiling ${srcs.length} Kotlin source to $out...")
    Tests.discover(frameworks, a0, log)._1
  }
}
