
object Gen {

  trait Generator[+T] {
    self => // self = this

    def generate: T

    def map[S](f: T => S): Generator[S] = {
      println(this + ".map()")

      new Generator[S] {
        def generate: S = {
          println(this + ".generate(), self:" + self)
          f(self.generate)
        }

        override def toString: String = "map()'s Gen"
      }
    }

    def flatMap[S](f: T => Generator[S]): Generator[S] = {
      println(this + ".flatMap()")

      new Generator[S] {
        def generate: S = {
          val v1 = self.generate
          println(this + ".generate(), self:" + self + " v1:" + v1)
          f(v1).generate
        }

        override def toString: String = "flatMap()'s Gen"
      }
    }
  }

  val integers: Generator[Int] = new Generator[Int] {
    val rand = new java.util.Random

    def generate = {
      val n = rand.nextInt()
      println("int.generate() " + n)
      n
    }

    override def toString: String = "intGen"
  }

  val booleans: Generator[Boolean] = new Generator[Boolean] {
    def generate = {
      val b = integers.generate > 0
      println("boo.generate() " + b)
      b
    }

    override def toString: String = "booGen"
  }

  def pairs[T, U](t: Generator[T], u: Generator[U]) =
    for {
      x <- t
      y <- u
    } yield (x, y)

  def single[T](x: T): Generator[T] = new Generator[T] {
    override def generate: T = x
  }

  def choose(low: Int, high: Int): Generator[Int] =
    for(x <- integers) yield low + x % (high - low)

  def oneOf[T](xs: T*): Generator[T] =
    for (idx <- choose(0, xs.length)) yield xs(idx)
}

import Gen._

//integers.generate
//booleans.generate

val v1 = for(x <- integers) yield  x % 10

v1.generate

//val v2 = integers.map(x => 1).generate
val v3 = integers.flatMap(x => integers)

v3.generate

val p =pairs(integers, booleans)

p.generate









