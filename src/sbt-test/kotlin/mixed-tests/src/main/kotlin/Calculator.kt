package demo

class Calculator(private val calculator: JavaCalculator) {

  fun sum(a: Int, b: Int): Int = calculator.sum(a, b)

}
