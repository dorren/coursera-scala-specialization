val vvv= List(Some(1),None,Some(2))

vvv.filter(_.nonEmpty).map(_.get)

None.getOrElse(1)

val a = Array(1, 2, 3)
a.updated(0, 10)

val list = List(('a', 1), ('b', 2), ('a', 3), ('b', 4))
val m = list.groupBy(_._1)
val m2 = m.map(x => (x._1, x._2.map(_._2).sum))
m2.toArray
m2.map(_._2).sum
