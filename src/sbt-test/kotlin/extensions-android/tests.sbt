import android.Keys._

val androidBuilder = TaskKey[com.android.builder.core.AndroidBuilder]("android-builder") in Android

TaskKey[Unit]("check-dex") := {
    implicit val out = outputLayout.value
    val p = androidBuilder.value
    val s = streams.value
    val layout = (projectLayout in Android).value
    val tools = p.getTargetInfo.getBuildTools.getLocation
    val dexdump = tools / "dexdump"
    val lines = Seq(
      dexdump.getAbsolutePath, "-i",
      (layout.dex / "classes.dex").getAbsolutePath).lines
    val hasKotlinClasses = lines map (_.trim) exists { l =>
      l.startsWith("Class descriptor") && l.endsWith("'Lkotlin/IntIterator;'")
    }
    if (!hasKotlinClasses) {
      lines filter (_.trim.startsWith("Class descriptor")) foreach (l => s.log.info(l))
      error("Kotlin classes not found")
    }
}
