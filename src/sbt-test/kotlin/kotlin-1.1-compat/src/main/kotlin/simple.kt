package demo
fun main(args: Array<String>) {
  val map = mapOf("key" to 42)
  val emptyMap = map - "key"

  val list1 = listOf("a", "b")
  val list2 = listOf("x", "y", "z")
  val minSize = minOf(list1.size, list2.size)
  val longestList = maxOf(list1, list2, compareBy { it.size })
}
