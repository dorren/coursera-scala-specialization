package observatory

import com.sksamuel.scrimage.Pixel
import observatory.Visualization.{interpolateColor, predictTemperature}
import org.apache.spark.rdd.RDD

/**
  * 4th milestone: value-added information
  */
object Manipulation {
  val spark = Extraction.spark

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    def gridTemp(gloc: GridLocation): Temperature = {
      val loc = Location(gloc.lat, gloc.lon)
      Visualization.predictTemperature(temperatures, loc)
    }

    gridTemp
  }

  
  var globalAverage: Map[GridLocation, Temperature] = Map.empty

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    def calculateTemp(gloc: GridLocation): Temperature = {
      val temps =
        temperaturess.map(yearData => {
          makeGrid(yearData)(gloc)
        })
      temps.sum / temps.size
    }

    def lookupTemp(gloc: GridLocation): Temperature = {
      val exists = globalAverage.exists(x => x._1 == gloc)
      if (exists) {
        globalAverage(gloc)
      }else {
        val temp = calculateTemp(gloc)
        globalAverage = globalAverage.updated(gloc, temp)
        temp
      }
    }

    lookupTemp
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

