package observatory

import com.sksamuel.scrimage.Pixel
import observatory.Visualization.{interpolateColor, predictTemperature}
import org.apache.spark.rdd.RDD

/**
  * 4th milestone: value-added information
  */
object Manipulation {
  var gridCache: Map[Int, Map[GridLocation, Temperature]] = Map.empty

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val key = temperatures.hashCode
    if (gridCache.exists(x => x._1 == key))
      gridCache(key)
    else {
      println(s"makeGrid ${temperatures}")
      val grid = (for {
        y <- -89 to 90
        x <- -180 to 179
      } yield {
        if(x == 0)
          println(s"makegrid ${x},${y}")
        val gloc = GridLocation(y, x)
        val temp = Visualization.predictTemperature(temperatures, Location(gloc.lat, gloc.lon))
        (gloc, temp)
      }).toMap

      gridCache = gridCache.updated(key, grid)

      (gloc: GridLocation) => {
        grid(gloc)
      }
    }
  }


  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val grids = temperaturess.map(x => makeGrid(x))

    def calculateTemp(gloc: GridLocation): Temperature = {
      val temps = grids.map(_(gloc))
      temps.sum / temps.size
    }

    calculateTemp
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)],
                normals: GridLocation => Temperature): GridLocation => Temperature = {
    def _deviation(gloc: GridLocation): Temperature = {
      makeGrid(temperatures)(gloc) - normals(gloc)
    }

    _deviation
  }


}

