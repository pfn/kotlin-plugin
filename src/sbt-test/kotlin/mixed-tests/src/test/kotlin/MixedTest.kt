import org.junit.Test
import junit.framework.TestCase.assertEquals
import demo.Calculator
import demo.JavaCalculator

class MixedTest {

  @Test
  fun `should sum 2 plus 2`() {
    val calculator = Calculator(JavaCalculator())
    assertEquals(4, calculator.sum(2, 2))
  }

}