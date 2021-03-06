package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    chars.foldLeft(0)((acc:Int, x:Char) =>
      if (acc >= 0 && x == '(')
        acc + 1
      else if( x == ')')
        acc - 1
      else
        acc
    ) == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int): (Int, Int) = {
      var acc = 0  // balanced accumulator, just like balance() above.
      var i = idx
      var r = 0    // count unbalanced ")"

      while(i < until){
        val x = chars(i)
        if (x == '(')
          acc += 1
        else if( x == ')') {
          if(acc > 0)
            acc -= 1
          else       // add up unbalanced ")" in variable r.
            r += 1
        }
        i += 1
      }
      //println("traverse ", chars.toList, idx, until, acc, r)
      (acc, r)
    }

    def reduce(from: Int, until: Int): (Int, Int) = {
      if(until - from <= threshold)
        traverse(from, until, 0, 0)
      else {
        val mid = (from + until) / 2
        val ((a1, r1), (a2, r2)) = parallel(reduce(from, mid), reduce(mid, until))
        //println("reduce A ", chars.toList, from, mid, until, " - ", a1, r1, a2, r2)

        (a1 - r2 + a2, r1)
      }
    }

    val (a, r) = reduce(0, chars.length)
    //println("reduce B ", chars.toList, a, r)
    a == 0 && r == 0
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
