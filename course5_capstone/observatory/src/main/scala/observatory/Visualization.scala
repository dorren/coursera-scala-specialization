package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import math._

/**
  * 2nd milestone: basic visualization
  */
object Visualization {
  // https://en.wikipedia.org/wiki/Great-circle_distance
  // https://www.movable-type.co.uk/scripts/latlong.html
  def greatCircleDistance(l1: Location, l2: Location): Double = {
    val R = 6371.0 // kilometers

    val φ1 = l1.lat.toRadians
    val φ2 = l2.lat.toRadians
    val Δφ = (l2.lat - l1.lat).toRadians
    val Δλ = (l2.lon - l1.lon).toRadians

    val a = sin(Δφ / 2) * sin(Δφ / 2) + cos(φ1) * cos(φ2) * sin(Δλ / 2) * sin(Δλ / 2)
    val c = 2 * atan2(Math.sqrt(a), sqrt(1 - a))

    val d = R * c
    d
  }


  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val withDist = temperatures.map(t => (t._1, t._2, greatCircleDistance(t._1, location)))
    val closeLocations = withDist.filter(x => x._3 <= 1.0)

    // https://en.wikipedia.org/wiki/Inverse_distance_weighting
    if(closeLocations.size > 0){
      closeLocations.head._2
    }else {
      val acc =
        withDist.foldLeft((0.0, 0.0))((z, x) => {
          val factor = 1.0 / pow(x._3, 2.0)
          (z._1 + factor * x._2, z._2 + factor)
        })
      acc._1 / acc._2
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val bottom = points.filter(p => value >= p._1).minBy(p => value - p._1)

    if(value == bottom._1)
      bottom._2
    else {
      val list = points.filter(p => p._1 > value)

      if(list.isEmpty)
        bottom._2
      else {
        val top = list.minBy(p => p._1 - value)
        val ratio = 1.0 * (value - bottom._1) / (top._1 - bottom._1)

        Color(
          bottom._2.red   + round(ratio * (top._2.red   - bottom._2.red)).toInt,
          bottom._2.green + round(ratio * (top._2.green - bottom._2.green)).toInt,
          bottom._2.blue  + round(ratio * (top._2.blue  - bottom._2.blue)).toInt
        )
      }
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val pixels =
      for {
        j <-   90 until -90 by -1
        i <- -180 until 180
      } yield {
        val temp = predictTemperature(temperatures, Location(j, i))
        val color = interpolateColor(colors, temp)
        Pixel(color.red, color.green, color.blue, 255)
      }

    Image(360, 180, pixels.toArray)
  }

}

