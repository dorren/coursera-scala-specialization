package observatory

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import org.scalatest.prop.Checkers
import observatory.Manipulation._
import TestData._
import observatory.Extraction._

trait ManipulationTest extends FunSuite with Checkers {
  test("makeGrid"){
    val gloc = GridLocation(41, -74)
    val actual = makeGrid(temperatures)(gloc)

    assert(actual === 4.5 +- 0.01)
  }

  test("average"){
    val years = Seq(temperatures, temperatures)
    val gloc = GridLocation(41, -74)
    val actual = average(years)(gloc)

    assert(actual === 4.5 +- 0.01)
  }

  test("average with csv"){
    val years = (1975 to 1986).map(year => {
      val list = locateTemperatures(year, "/stations.csv", s"/${year}.csv")
      locationYearlyAverageRecords(list)
    })

    val gloc = GridLocation(41, -74)
    val actual = average(years)(gloc)
    println(actual)
  }
}