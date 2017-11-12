package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import observatory.Extraction._

trait ExtractionTest extends FunSuite {

  test("tempConvert") {
    assert(tempConvert(32) === 0)
    assert(tempConvert(42) === 5.6)
    assert(tempConvert(0) === -17.8)
  }

  test("fsPath") {
    assert(!(fsPath("/1975.csv").isEmpty))
  }

  test("stationDF") {
    val df = stationDF("/stations.csv")
    df.show
  }

  test("temperatureDF") {
    val df = temperatureDF("/1975.csv")
    df.show
  }

  test("locateTemperatures") {
    val list = locateTemperatures(1975, "/stations.csv", "/1975.csv")
    println("locateTemperatures " + list.size)
    println(list.take(30))
  }

  test("locationYearlyAverageRecords") {
    val list = locateTemperatures(1975, "/stations.csv", "/1975.csv")
    val avgs = locationYearlyAverageRecords(list)
    println(avgs.take(30))
  }

  test("yearlyAverageCombined") {
    val avgs = yearlyAverageCombined(1975, "/stations.csv", "/1975.csv")
    println(avgs.take(30))
  }
}