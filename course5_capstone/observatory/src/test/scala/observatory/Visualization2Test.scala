package observatory

import java.io.File

import Extraction.{spark, yearlyAverageCombined}
import Manipulation.{deviation, average}
import Visualization2._
import observatory.Visualization.pathMkdir
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait Visualization2Test extends FunSuite with Checkers {
  test("visualizeGrid"){
    spark.sparkContext.setLogLevel("ERROR")

    val years = (1988 to 1990).map(year => {
      yearlyAverageCombined(year, "/stations.csv", s"/${year}.csv")
    })

    val year = 1991
    val y1991 = yearlyAverageCombined(year, "/stations.csv", s"/${year}.csv")

    val grid2TempFn = deviation(y1991, average(years))
    val tile = Tile(1,1,2)

    val image = visualizeGrid(grid2TempFn, deviationColor, tile)
    val path = "target/temp_deviation.png"
    pathMkdir(path)
    image.output(new File(path))
  }
}
