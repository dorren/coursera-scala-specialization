import org.scalacheck.Prop.forAll
import org.scalacheck._
import Arbitrary.arbitrary
import quickcheck.IntHeap
import org.scalacheck.Gen._

//val propSqrt = forAll {
//  (n: Int) => scala.math.sqrt(n*n) == n
//}
//
//propSqrt.check
//
//val numGen1 = Gen.choose(1, 10)
//val genCheck = Prop.forAll(numGen1) { n =>
//  n >= 1 && n <= 10
//}
//genCheck.check
//
//val g2 = oneOf(1,2,3)
//g2.sample
//
//val g2Check = forAll(g2) { n =>
//  n >= 1 && n <=3
//}
//g2Check.check
//
//val g3 = arbitrary[Int]
//g3.sample
//g3.sample
//g3.sample
//
//val genIntList = Gen.containerOf[List,Int](Gen.oneOf(1, 3, 5))
//genIntList.sample
//
//val evenInteger = Arbitrary.arbitrary[Int] suchThat ( _ % 2 == 0)
//evenInteger.sample
//evenInteger.sample
//evenInteger.sample
//evenInteger.sample
//
//Gen.const(1).sample
//
//Gen.oneOf(1,3,5)
//
//val numGen = Gen.chooseNum(1, 1000)
//numGen.sample getOrElse 0
//numGen.sample getOrElse 0
//
//// uniqGen = new UniqGen
//// uniqGen.sample
//
class UniqGen(min: Int, max: Int) {
  val numGen = Gen.chooseNum(min, max)
  private var generated: Set[Int] = Set.empty

  def sample: Int = {
    if(generated.size > max - min)
      throw new ArrayIndexOutOfBoundsException("unique numbers exhausted")

    var n = numGen.sample getOrElse 0
    if(generated contains n)
      n = sample
    else
      generated = generated + n
    n
  }
}

val u1 = new UniqGen(1, 2)
u1.sample
u1.sample
//u1.sample


val genMap: Gen[Map[Int,Int]] = {
  def inner: Gen[Map[Int, Int]] =
    for {
      k <- arbitrary[Int]
      v <- arbitrary[Int]
      m <- oneOf(const(Map.empty[Int,Int]), genMap)
    } yield {
      println("m is " + k + ","+ v + "," + m.updated(k, v))
      m.updated(k, v)
    }

  oneOf( const(Map.empty[Int,Int]), inner )
}

for( i <- 1 to 6) {
  println(genMap.sample)
}


Gen.containerOf[List,Int](Gen.oneOf(1, 3, 5)).sample

