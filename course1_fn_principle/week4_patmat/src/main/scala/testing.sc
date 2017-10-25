import patmat.Huffman._
import sun.misc.Signal

List(1,2,3) ++ List(4,5,6)
val l = List(1,2,3) ::: List(4,5,6)
val (a, b) = l.splitAt(2)
a
b

val r = 2 until 3
r

var l2 = for {
  i <- 1 to 3; j <- 1 to i
} yield (i, j)

println(l2)

List(1,3,2,5,4,3).toSet



