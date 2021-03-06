package demo
fun main(args: Array<String>) {
  // Test some Kotlin 1.2 features
  val items = (1..9).map { it * it }

  val chunkedIntoLists = items.chunked(4)
  val points3d = items.chunked(3) { (x, y, z) -> Triple(x, y, z) }
  val windowed = items.windowed(4)
  val slidingAverage = items.windowed(4) { it.average() }
  val pairwiseDifferences = items.zipWithNext { a, b -> b - a }

  println("Hello, world!")
}
