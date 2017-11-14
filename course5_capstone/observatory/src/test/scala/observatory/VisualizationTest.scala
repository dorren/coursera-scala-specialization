package observatory


import java.io.File

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import observatory.Visualization._

import math.round
import TestData._

trait VisualizationTest extends FunSuite with Checkers {
  test("interpolateColor"){
    assert(interpolateColor(temp2color, 70) === Color(255,255,255))
    assert(interpolateColor(temp2color, 60) === Color(255,255,255))
    assert(interpolateColor(temp2color, 50) === Color(255,164,164))
    assert(interpolateColor(temp2color, 32) === Color(255,  0,  0))
    assert(interpolateColor(temp2color, -5) === Color(  0,170,255))
    assert(interpolateColor(temp2color,-27) === Color(255,  0,255))
  }

  test("interpolateColor2"){
    import Visualization2.deviationColor
    println(interpolateColor(deviationColor, 1.53))
  }

  test("interpolateColor 3"){
    val temp2colors = List((-100.0,Color(255,0,0)), (100.0,Color(0,0,255)))
    println(interpolateColor(temp2colors,  0.05))
    println(interpolateColor(temp2colors, -0.05))
  }

  test("greatCircleDistance"){
    val d = greatCircleDistance(flushing, la)
    assert(round(d) === 3990)
    assert(greatCircleDistance(flushing, flushing) === 0.0)
  }

  test("predictTemperature"){
    val temps = Seq((manhattan, 40.0),
                    (flushing, 35.0),
                    (la, 75.0)
                )

    var t = predictTemperature(temps, flushing)
    assert(t === 35.0)

    t = predictTemperature(temps, chicago)
    println("Chicago " + t)

    t = predictTemperature(temps, sfo)
    println("San Francisco " + t)
  }

  test("visualize"){
    val image = visualize(temperatures, temp2color)

    val path = "target/temperatures/1900/0/global_temperature.png"
    pathMkdir(path)
    image.output(new File(path))
  }
}
