package demo

abstract class SomeClass<T> {
  abstract fun execute(): T
}

class SomeImplementation : SomeClass<String>() {
  override fun execute(): String = "Test"
}

class OtherImplementation : SomeClass<Int>() {
  override fun execute(): Int = 42
}

object Runner {
  inline fun <reified S: SomeClass<T>, T> run(): T {
    return S::class.java.getDeclaredConstructor().newInstance().execute()
  }
}

// Test some Kotlin 1.7 features
fun main() {
  // T is inferred as String because SomeImplementation derives from SomeClass<String>
  val s = Runner.run<SomeImplementation, _>()
  assert(s == "Test")

  // T is inferred as Int because OtherImplementation derives from SomeClass<Int>
  val n = Runner.run<OtherImplementation, _>()
  assert(n == 42)
}
