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

  var globalAverage: RDD[(GridLocation, Temperature)] = spark.sparkContext.parallelize(Seq.empty)

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    def calculateTemp(gloc: GridLocation): Temperature = {
      val temps =
        temperaturess.map(yearData => {
          val sample = yearData.take(1).head
          println("Manipulate.avg.calc " + sample)
          makeGrid(yearData)(gloc)
        })
      temps.sum / temps.size
    }

    def lookupTemp(gloc: GridLocation): Temperature = {
      println("Manipulate.avg.lookupTemp " + gloc)
      val list = globalAverage.filter(x => x._1 == gloc).collect
      if (!list.isEmpty) {
        list.take(1).head._2
      }else {
        val temp = calculateTemp(gloc)
        val newRDD = spark.sparkContext.parallelize(Seq((gloc, temp)))
        globalAverage = globalAverage.union(newRDD)
        println("Manipulate.avg.lookupTemp2 " + gloc + ", temp " + temp)
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
    ???
  }


}

