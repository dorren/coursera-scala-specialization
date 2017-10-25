import streams.Bloxorz._
import streams.{GameDef, Solver, StringParserTerrain}

trait SolutionChecker extends GameDef with Solver with StringParserTerrain {
  /**
    * This method applies a list of moves `ls` to the block at position
    * `startPos`. This can be used to verify if a certain list of moves
    * is a valid solution, i.e. leads to the goal.
    */
  def solve(ls: List[Move]): Block =
    ls.foldLeft(startBlock) { case (block, move) =>
      require(block.isLegal) // The solution must always lead to legal blocks
      move match {
        case Left => block.left
        case Right => block.right
        case Up => block.up
        case Down => block.down
      }
    }
}

trait Level1 extends SolutionChecker {
  /* terrain for level 1*/

  val level =
    """ooo-------
      |oSoooo----
      |ooooooooo-
      |-ooooooooo
      |-----ooToo
      |------ooo-""".stripMargin

  val optsolution = List(Right, Right, Down, Right, Right, Right, Down)
}

val game = new Level1 {

}

val stream = 1 #:: 2 #:: 3 #::4 #:: Stream.empty

val res = for{
  x <- stream
  //if x % 2 == 0
} yield x
res.map(x => x)

def from(n: Int): Stream[Int] =
  n #:: from(n+1)

def even(s :Stream[Int]): Stream[Int] =
  s.head #:: even(s.tail filter (_ % 2 == 0))

even(from(2))

val d = from(2) #::: from(2)
d.take(6).toList




