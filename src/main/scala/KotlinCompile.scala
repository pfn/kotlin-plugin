package kotlinplugin

import java.io.File

import com.sampullara.cli.Args
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.{CompilerMessageLocation, CompilerMessageSeverity, MessageCollector}
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import sbt.Keys.{TaskStreams, Classpath}
import sbt._

import language.postfixOps
import collection.JavaConverters._

/**
 * @author pfnguyen
 */
object KotlinCompile {
  case class CompilerLogger(log: Logger) extends MessageCollector {
    override def report(severity: CompilerMessageSeverity,
                        message: String,
                        location: CompilerMessageLocation) = {
      val logger = severity match {
        case e if CompilerMessageSeverity.ERRORS.contains(e) => log.error(_: String)
        case v if CompilerMessageSeverity.VERBOSE.contains(v) => log.verbose(_: String)
        case CompilerMessageSeverity.INFO => log.info(_: String)
        case CompilerMessageSeverity.WARNING => log.warn(_: String)
      }

      val path = location.getPath
      if (path != null) {
        logger(s"$path: ${location.getLine}, ${location.getColumn}: $message")
      } else
        logger(message)
    }
  }
  val compiler = new K2JVMCompiler
  def compilerArgs = new K2JVMCompilerArguments

  def compile(options: Seq[String],
              kotlinSource: File, extraSources: Seq[File],
              classpath: Classpath,
              output: File, s: TaskStreams): Unit = {
    val args = compilerArgs
    val kotlinFiles = "*.kt" || "*.kts"

    val sources = extraSources.flatMap(_ ** kotlinFiles get).distinct
    if (sources.isEmpty) {
      s.log.debug("No kotlin sources found, skipping kotlin compile")
    } else {
      s.log.info(s"Compiling ${sources.size} kotlin source file(s)")
      args.freeArgs = sources.map(_.getAbsolutePath).asJava

      args.noStdlib = true
      args.noJdkAnnotations = true
      Args.parse(args, options.toArray)
      val cp = classpath.map(_.data.getAbsoluteFile).mkString(File.pathSeparator)
      args.classpath = Option(args.classpath).fold(cp)(_ + File.pathSeparator + cp)
      args.destination = output.getAbsolutePath
      // bug in scalac prevents calling directly, yuck
      val r = KotlinCompileJava.compile(CompilerLogger(s.log), compiler, args)
      r match {
        case ExitCode.COMPILATION_ERROR | ExitCode.INTERNAL_ERROR =>
          throw new MessageOnlyException("Compilation failed. See log for more details")
        case _ =>
      }
    }
  }

}
