package demo

// Test some Kotlin 1.6 features
fun getSuspending(suspending: suspend () -> Unit) {}

fun suspending() {}

@Target(AnnotationTarget.TYPE_PARAMETER)
annotation class BoxContent

class Box<@BoxContent T> {}

fun main() {
  getSuspending { }           // OK
  getSuspending(::suspending) // OK
}
