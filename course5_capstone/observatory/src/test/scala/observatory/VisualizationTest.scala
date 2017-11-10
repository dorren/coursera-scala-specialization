package observatory


import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import observatory.Visualization._
import math.round

trait VisualizationTest extends FunSuite with Checkers {
  val temp2color =
    Seq(( 60.0, Color(255,255,255)),
      ( 32.0, Color(255,  0,  0)),
      ( 12.0, Color(255,255,  0)),
      (  0.0, Color(  0,255,255)),
      (-15.0, Color(  0,  0,255)),
      (-27.0, Color(255,  0,255)),
      (-50.0, Color( 33,  0,107)),
      (-60.0, Color(  0,  0,  0))
    )

  test("interpolateColor"){
    assert(interpolateColor(temp2color, 70) === Color(255,255,255))
    assert(interpolateColor(temp2color, 60) === Color(255,255,255))
    assert(interpolateColor(temp2color, 50) === Color(255,164,164))
    assert(interpolateColor(temp2color, 32) === Color(255,  0,  0))
    assert(interpolateColor(temp2color, -5) === Color(  0,170,255))
    assert(interpolateColor(temp2color,-27) === Color(255,  0,255))
  }

  test("greatCircleDistance"){
    val l1 = Location(40.7611869, -73.8278084)   // Flushing NY
    val l2 = Location(34.0201597, -118.6926134)  // LA

    val d = greatCircleDistance(l1, l2)
    assert(round(d) === 3990)
    assert(greatCircleDistance(l1, l1) === 0.0)
  }

  test("predictTemperature"){
    val manhattan = Location(40.7627608,-73.9633166)    // central park, NY
    val flushing  = Location(40.7611869, -73.8278084)   // Flushing NY
    val la        = Location(34.0201597, -118.6926134)  // LA
    val chicago   = Location(41.8333925,-88.0121478)    // Chicago
    val sfo       = Location(37.7576792,-122.5078121)   // San Francisco

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
    val manhattan = Location(40.7627608,  -73.9633166)    // central park, NY
    val flushing  = Location(40.7611869,  -73.8278084)    // Flushing NY
    val la        = Location(34.0201597, -118.6926134)    // LA
    val chicago   = Location(41.8333925,  -88.0121478)    // Chicago
    val sfo       = Location(37.7576792, -122.5078121)    // San Francisco
    val shanghai  = Location(31.6140163,  111.1240098)    // Shanghai
    val sahara    = Location(20.6989319,    7.9953434)    // sahara
    val mteverest = Location(27.9878493,   86.9162499)    // Mt. Everest
    val antarctica= Location(-75.966502,    3.601225)     // Antarctica

    val temps = Seq((manhattan, 4.5),
                    (la, 30.0),
                    (sfo, 24.0),
                    (shanghai, 18.3),
                    (sahara, 49.0),
                    (mteverest, -34.5),
                    (antarctica,-60.0)
                   )
    val image = visualize(temps, temp2color)
    image.output(new java.io.File("target/global_temperature.png"))
  }
}
