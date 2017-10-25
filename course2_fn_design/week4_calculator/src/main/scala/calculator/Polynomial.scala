package calculator

// https://www.math10.com/en/algebra/quadratic-equation.html
object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = {
    Signal(b() * b() - 4 * a() * c() )
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
      c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = {
    Signal {
      val d = delta()

      if (d < 0)
        Set()
      else if (d == 0.0)
        Set((-b() / 2.0 / a()))
      else {
        val s1 = (-b() + Math.sqrt(d)) / 2.0 / a()
        val s2 = (-b() - Math.sqrt(d)) / 2.0 / a()

        Set(s1, s2)
      }
    }
  }
}
