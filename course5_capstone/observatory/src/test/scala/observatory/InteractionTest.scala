package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap
import Interaction._
import Visualization.temp2color
import TestData._
import observatory.Extraction._

import scala.math.{Pi, atan, sinh}

trait InteractionTest extends FunSuite with Checkers {
  val latMax = atan(sinh(Pi)).toDegrees

  test("tileLocation"){
    assert(tileLocation(Tile(0, 0, 0)) === Location(latMax, -180.0))
    assert(tileLocation(Tile(1, 1, 1)) === Location(0.0, 0.0))

    val lat = atan(sinh(Pi - 2.0 * Pi / 4)).toDegrees
    assert(tileLocation(Tile(1, 1, 2)) === Location(lat, -90.0))
  }

  test("zoomTile"){
    val tile = Tile(0,0,0)
    val tiles = zoomTile(tile, 2)
    println(tiles)
  }

  test("tile"){
    var t = Tile(0,0,0)
    var image = tile(temperatures, temp2color, t)
    image.output(new java.io.File(s"target/tile_${t.x}_${t.y}_${t.zoom}.png"))

    t = Tile(1,1,1)
    image = tile(temperatures, temp2color, t)
    image.output(new java.io.File(s"target/tile_${t.x}_${t.y}_${t.zoom}.png"))
  }

  test("generateImage"){
    val list = locateTemperatures(1975, "/stations.csv", "/1975.csv")
    val avgs = locationYearlyAverageRecords(list)
    println(avgs.take(30))

    generateImage(1975, Tile(0,0,0), avgs)
  }

  test("generateTiles"){
    val years = (1991 to 2015)
    years.foreach(year => {
      val avgs = yearlyAverageCombined(year, "/stations.csv", s"/${year}.csv")
      generateTiles(Seq((year, avgs)), generateImage)
    })
  }

}
