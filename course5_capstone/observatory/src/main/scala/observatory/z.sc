val str = "007034,,,"
val list = str.split(",").toList

val a = List(1,2,3,4)
1.0 * a.sum / a.length
6371e3

10 until 1 by -2

import math._
import scala.reflect.io.Path
atan(sinh(Pi)) * 180.0 / Pi
atan(sinh(Pi)).toDegrees

var m1 = Map("a" -> 1, "b" -> 2)
m1.find(_._1 == "b").get._2
m1 = m1.updated("c", 3)
m1.exists(x => x._1 == "c")

(1975 to 1990)

List(List(5,6), List(4)).flatMap(x => x)
val list1 = List(1,2,3,4,5)


import org.scalatest.Matchers._
4.5 === 4.502 +- 0.01


1.2.floor
1.2.ceil

import java.io.File
val file = new File("target/xyz/abc.txt")
file.exists()

val (v1, v2) = List(1,2,3).par.map(x => (x*x*x, x))
                          .reduce((a, b) =>(a._1+ b._1, a._2 + b._2))

var closeLocations: List[(Int, Int)] = List.empty
closeLocations = closeLocations :+ ((4,5))

import observatory.Signal
Signal(1)() == Signal(1)()

import observatory.Visualization.temp2color
temp2color

