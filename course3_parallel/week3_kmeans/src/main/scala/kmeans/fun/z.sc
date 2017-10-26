import kmeans.Point

val m: Map[Int, Int] = Map.empty
m.contains(1)
m.empty
(1 to 6).toList.foldLeft(m)((z, x) => z + (x -> (x + 10)))

val l = List(1,2,3)
val n = List(4,5,6)

l.zip(n).forall(x => x._1 < x._2)
l(0).equals(0)

val p1 = new Point(0.0, 0.0, 0.0)
val p2 = new Point(0.0, 0.0, 0.0)
p1.equals(p2)
p1 == p2
