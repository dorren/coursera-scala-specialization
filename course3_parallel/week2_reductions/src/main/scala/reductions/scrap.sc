val l = List(1, 2, 3)
val a = l.toArray
0 +: a

l.reduce((a, b) => a max b)
a.scanLeft(10){(acc, x) => { acc + x } }

(0 until 3).map(_ % 100 * 1.0f).toArray

import reductions.LineOfSight._
val l1 = new Leaf(0, 2, 5)
val l2 = new Leaf(2, 4, 10)
val n1 = new Node(l1, l2)
n1.maxPrevious