package kotlinplugin

import java.io.File
import java.util.jar.JarEntry

import com.sampullara.cli.Args
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import sbt.Keys.{TaskStreams, Classpath}
import sbt._

import collection.JavaConverters._

/**
 * @author pfnguyen
 */
object KotlinCompile {
  def compilerArgs = new K2JVMCompilerArguments

  def grepjar(jarfile: File)(pred: JarEntry => Boolean): Boolean =
    jarfile.isFile && Using.jarFile(false)(jarfile) { in =>
      in.entries.asScala exists pred
    }

  def compile(options: Seq[String],
              sourceDirs: Seq[File],
              kotlinPluginOptions: Seq[String],
              classpath: Classpath,
              output: File, s: TaskStreams): Unit = {
    val args = compilerArgs
    val kotlinFiles = "*.kt" || "*.kts"
    val javaFiles = "*.java"

    val kotlinSources = sourceDirs.flatMap(d => (d ** kotlinFiles).get.distinct)
    val javaSources = sourceDirs.filterNot(f => sourceDirs.exists(f0 =>
      f0.relativeTo(f).isDefined && f != f0)) map (d =>
      (d, (d ** javaFiles).get)) filter (_._2.nonEmpty)
    if (kotlinSources.isEmpty) {
      s.log.debug("No sources found, skipping kotlin compile")
    } else {
      def pluralizeSource(count: Int) =
        if (count == 1) "source" else "sources"
      val message =
        s"Compiling ${kotlinSources.size} Kotlin ${pluralizeSource(kotlinSources.size)}"
      s.log.info(message)
      args.freeArgs = (kotlinSources ++ javaSources.map(_._1)).map(_.getAbsolutePath).asJava

      args.noStdlib = true
      Args.parse(args, options.toArray)
      val fcpjars = classpath.map(_.data.getAbsoluteFile)
      val (pluginjars, cpjars) = fcpjars.partition {
        grepjar(_)(_.getName.startsWith(
          "META-INF/services/org.jetbrains.kotlin.compiler.plugin"))
      }
      val cp = cpjars.mkString(File.pathSeparator)
      val pcp = pluginjars.map(_.getAbsolutePath).toArray
      args.classpath = Option(args.classpath).fold(cp)(_ + File.pathSeparator + cp)
      args.pluginClasspaths = Option(args.pluginClasspaths).fold(pcp)(_ ++ pcp)
      args.pluginOptions = Option(args.pluginOptions).fold(
        kotlinPluginOptions.toArray)(_ ++ kotlinPluginOptions.toArray[String])
      output.mkdirs()
      args.destination = output.getAbsolutePath
      // bug in scalac prevents calling directly, yuck
      KotlinCompileJava.compile(s.log, args)
    }
  }

}
