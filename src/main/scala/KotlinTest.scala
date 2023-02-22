package sbt

import sbt.Keys.*
import sbt.internal.inc.classfile.JavaAnalyze
import sbt.internal.inc.classpath.ClasspathUtil
import sbt.internal.inc.*
import xsbti.{VirtualFile, VirtualFileRef}
import xsbti.compile.*

object KotlinTest {
  private object EmptyLookup extends Lookup {
    def changedClasspathHash: Option[Vector[FileHash]] = None

    def analyses: Vector[CompileAnalysis] = Vector.empty
    def lookupOnClasspath(binaryClassName: String): Option[VirtualFileRef] = None
    def lookupAnalysis(binaryClassName: String): Option[CompileAnalysis] = None
    def changedBinaries(previousAnalysis: xsbti.compile.CompileAnalysis): Option[Set[VirtualFileRef]] = None
    def changedSources(previousAnalysis: xsbti.compile.CompileAnalysis): Option[xsbti.compile.Changes[VirtualFileRef]] = None
    def removedProducts(previousAnalysis: xsbti.compile.CompileAnalysis): Option[Set[VirtualFileRef]] = None
    def shouldDoIncrementalCompilation(changedClasses: Set[String],analysis: xsbti.compile.CompileAnalysis): Boolean = true
    override def hashClasspath(classpath: Array[VirtualFile]): java.util.Optional[Array[FileHash]] = java.util.Optional.empty()
  }

  val kotlinTests = Def.task {
    val out = ((Test / target).value ** "scala-*").get.head / "test-classes"
    val srcs = ((Test / sourceDirectory).value ** "*.kt").get.map(f => PlainVirtualFile(f.toPath())).toList
    val xs = (out ** "*.class").get.toList

    val loader = ClasspathUtil.toLoader((Test / fullClasspath).value map {
      _.data
    })

    val log = streams.value.log
    val output = new SingleOutput {
      def getOutputDirectory: File = out
    }

    val so = (Test / scalacOptions).value
    val jo = (Test / javacOptions).value
    val c = (Test / compile).value

    val incremental = Incremental(
      srcs.toSet,
      PlainVirtualFileConverter.converter,
      EmptyLookup,
      Analysis.Empty,
      IncOptions.of(),
      MiniSetup.of(
        output,
        MiniOptions.of(null, so.toArray, jo.toArray),
        null,
        null,
        false,
        null
      ),
      c.readStamps(),
      output,
      JarUtils.createOutputJarContent(output),
      None,
      None,
      None,
      log,
    )((_, _, callback, _) => {
      def readAPI(source: VirtualFileRef, classes: Seq[Class[_]]): Set[(String, String)] = {
        val (apis, mainClasses, inherits) = ClassToAPI.process(classes)
        apis.foreach(callback.api(source, _))
        mainClasses.foreach(callback.mainClass(source, _))
        inherits.map {
          case (from, to) => (from.getName, to.getName)
        }
      }
      JavaAnalyze(
        xs.map(_.toPath),
        srcs,
        log,
        output,
        None
      )(callback, loader, readAPI)
    })

    val frameworks = (Test / loadedTestFrameworks).value.values.toList
    log.info(s"Compiling ${srcs.length} Kotlin source to $out...")
    Tests.discover(frameworks, incremental._2, log)._1
  }
}
