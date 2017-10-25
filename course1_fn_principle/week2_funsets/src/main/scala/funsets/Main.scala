package funsets

object Main extends App {
  import FunSets._
  println(FunSets.toString(union(singletonSet(1), singletonSet(2))))
  println(contains(singletonSet(1), 1))
}
