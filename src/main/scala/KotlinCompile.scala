package kotlin

import java.io.File
import java.lang.reflect.Method
import java.util.jar.JarEntry

import sbt.Keys.{Classpath, TaskStreams}
import sbt._
import sbt.classpath.ClasspathUtilities

import collection.JavaConverters._

/**
 * @author pfnguyen
 */
object KotlinCompile {

  def grepjar(jarfile: File)(pred: JarEntry => Boolean): Boolean =
    jarfile.isFile && Using.jarFile(false)(jarfile) { in =>
      in.entries.asScala exists pred
    }

  def compile(options: Seq[String],
              sourceDirs: Seq[File],
              kotlinPluginOptions: Seq[String],
              classpath: Classpath,
              compilerClasspath: Classpath,
              output: File, s: TaskStreams): Unit = {
    import language.reflectiveCalls
    val stub = KotlinStub(s, compilerClasspath)
    val args = stub.compilerArgs
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

      stub.parse(args.instance, options.toArray)
      val fcpjars = classpath.map(_.data.getAbsoluteFile)
      val (pluginjars, cpjars) = fcpjars.partition {
        grepjar(_)(_.getName.startsWith(
          "META-INF/services/org.jetbrains.kotlin.compiler.plugin"))
      }
      val cp = cpjars.mkString(File.pathSeparator)
      val pcp = pluginjars.map(_.getAbsolutePath).toArray
      args.classpath = Option(args.classpath[String]).fold(cp)(_ + File.pathSeparator + cp)
      args.pluginClasspaths = Option(args.pluginClasspaths[Array[String]]).fold(pcp)(_ ++ pcp)
      args.pluginOptions = Option(args.pluginOptions[Array[String]]).fold(
        kotlinPluginOptions.toArray)(_ ++ kotlinPluginOptions.toArray[String])
      output.mkdirs()
      args.destination = output.getAbsolutePath
      stub.compile(args.instance)
    }
  }
}

case class KotlinStub(s: TaskStreams, cp: Seq[Attributed[File]]) {
  import language.reflectiveCalls
  val cl = ClasspathUtilities.toLoader(cp map (_.data))
  val servicesClass = cl.loadClass("org.jetbrains.kotlin.config.Services")
  val compilerClass = cl.loadClass("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler")
  val compilerArgsClass = cl.loadClass(
    "org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments")
  val messageCollectorClass = cl.loadClass(
    "org.jetbrains.kotlin.cli.common.messages.MessageCollector")
  val commonCompilerArgsClass = cl.loadClass(
    "org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments")
  val compilerExec = compilerClass.getMethod("exec",
    messageCollectorClass, servicesClass, commonCompilerArgsClass)
  val servicesEmptyField = servicesClass.getDeclaredField("EMPTY")
  val argsClass = cl.loadClass(
    "org.jetbrains.kotlin.relocated.com.sampullara.cli.Args")
  val argsParse = argsClass.getMethod("parse", classOf[Object], classOf[Array[String]])

  def parse(args: Object, options: Array[String]): Unit = {
    argsParse.invoke(null, args, options)
  }
  def messageCollector: AnyRef = {
    type CompilerMessageLocation = {
      def getPath: String
      def getLine: Int
      def getColumn: Int
    }

    import java.lang.reflect.{Proxy, InvocationHandler}
    val messageCollectorInvocationHandler = new InvocationHandler {
      override def invoke(proxy: scala.Any, method: Method, args: Array[AnyRef]) = {
        if (method.getName == "report") {
          val Array(severity, message, location) = args
          val l = location.asInstanceOf[CompilerMessageLocation]
          severity.toString match {
            case "INFO"                 => s.log.info(message.toString)
              val msg = Option(l.getPath).fold(message.toString)(loc =>
                loc + ": " + l.getLine + ", " + l.getColumn + ": " + message)
              s.log.info(msg)
            case "WARNING"              => s.log.warn(message.toString)
            case "ERROR"  | "EXCEPTION" => s.log.error(message.toString)
            case "OUTPUT" | "LOGGING"   => s.log.debug(message.toString)
          }
        }
        null
      }
    }

    Proxy.newProxyInstance(cl, Array(messageCollectorClass), messageCollectorInvocationHandler)
  }

  def compilerArgs = {
    import language.dynamics
    new Dynamic {
      def selectDynamic[A](field: String): A = {
        val f = compilerArgsClass.getField(field)
        f.get(instance).asInstanceOf[A]
      }
      def updateDynamic(field: String)(value: Any): Unit = {
        val f = compilerArgsClass.getField(field)
        f.set(instance, value)
      }
      val instance = compilerArgsClass.newInstance().asInstanceOf[AnyRef]
    }
  }
  def compile(args: AnyRef): Unit = {
    val compiler = compilerClass.newInstance()
    val result = compilerExec.invoke(compiler,
      messageCollector, servicesEmptyField.get(null), args: java.lang.Object)
    result.toString match {
      case "COMPILATION_ERROR" | "INTERNAL_ERROR" =>
        throw new MessageOnlyException("Compilation failed. See log for more details")
      case _ =>
    }
  }
}
