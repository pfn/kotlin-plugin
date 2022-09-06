package demo

sealed interface Polygon
sealed interface Fillable

class FilledRectangle: Polygon, Fillable

@JvmInline
value class Password(val s: String)

// Test some Kotlin 1.5 features
fun main() {
  val rectangle = FilledRectangle()

  val password = Password("")
}
