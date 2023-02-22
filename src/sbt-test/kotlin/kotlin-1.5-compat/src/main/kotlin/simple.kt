package demo
fun main(args: Array<String>) {
  // Test some Kotlin 1.3 features
  val keys = 'a'..'f'
  val map = keys.associateWith { it.toString().repeat(5).capitalize() }
  map.forEach { println(it) }
}
