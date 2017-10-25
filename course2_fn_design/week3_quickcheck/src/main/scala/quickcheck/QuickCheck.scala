package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  // gen a list of numbers
  // insert into heap
  lazy val genHeap: Gen[H] = oneOf(
    const(empty),
    for {
      int <- arbitrary[A]
      heap <- frequency((1, const(empty)), (7, genHeap))
    } yield insert(int, heap))

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)


  property("findMin on empty") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("insert any two elements into an empty heap, the minimum of the resulting heap " +
           " should be the smallest of the two elements.") = forAll { (x: A, y: A) =>
    val h = empty
    findMin(insert(x, insert(y, h))) == (x min y)
  }

  property("If you insert an element into an empty heap, then delete the minimum, " +
           "the resulting heap should be empty.") = forAll { (x: A) =>
    val h = empty
    deleteMin(insert(x, h)) == empty
  }

  property("Given any heap, you should get a sorted sequence of elements when continually " +
           "finding and deleting minima. " +
           "(Hint: recursion and helper functions are your friends.)") = forAll { (h: H) =>
    // deleteMin one by one. convert heap to list
    def strip(h: H): List[A] =
      h match {
        case empty => Nil
        case _ => {
          val m = findMin(h)
          m :: strip(deleteMin(h))
        }
      }

    val list = strip(h)
    list.sorted == list
  }

  property("Finding a minimum of the melding of any two heaps should " +
           "return a minimum of one or the other.") = forAll { (h1: H, h2: H) =>
    if(h1 == empty || h2 == empty)
      true
    else {
      val min1 = findMin(h1)
      val min2 = findMin(h2)

      findMin(meld(h1, h2)) == (min1 min min2)
    }
  }

  property("assure no node lost during operations") = forAll { (h1: H, h2:H) =>
    if(h1 == empty || h2 == empty)
      true
    else {
      val min1 = findMin(h1)
      val min2 = findMin(h2)
      val m = min1 min min2

      findMin(meld(deleteMin(h1), insert(m, h2))) == m
    }
  }

  property("deep compare modified heap") = forAll { (h1: H, h2: H) =>
    def deepEqual(h1: H, h2: H): Boolean =
      if (h1 == empty && h2 == empty) true
      else {
        val m1 = findMin(h1)
        val m2 = findMin(h2)
        m1 == m2 && deepEqual(deleteMin(h1), deleteMin(h2))
      }

    if (h1 == empty || h2 == empty) true
    else {
      deepEqual(meld(h1, h2),
                meld(deleteMin(h1), insert(findMin(h1), h2)))
    }
  }
}
