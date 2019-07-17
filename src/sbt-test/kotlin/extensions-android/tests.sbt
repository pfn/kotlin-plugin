import android.Keys._
import sys.process._

val androidBuilder = SettingKey[Logger => com.android.builder.core.AndroidBuilder]("android-builder") in Android

TaskKey[Unit]("checkDex") := {
    implicit val out = outputLayout.value
    val p = androidBuilder.value
    val s = streams.value
    val layout = (projectLayout in Android).value
    val tools = p(s.log).getTargetInfo.getBuildTools.getLocation
    val dexdump = tools / "dexdump"
    val lines = Seq(
      dexdump.getAbsolutePath, "-i",
      (layout.dex / "classes.dex").getAbsolutePath).lineStream
    val hasKotlinClasses = lines map (_.trim) exists { l =>
      l.startsWith("Class descriptor") && l.endsWith("'Lkotlin/Unit;'")
    }
    if (!hasKotlinClasses) {
      lines filter (_.trim.startsWith("Class descriptor")) foreach (l => s.log.info(l))
      sys.error("Kotlin classes not found")
    }
}
