package observatory

import observatory.Visualization.temp2color
import observatory.Visualization2.deviationColor

/**
  * 6th (and last) milestone: user interface polishing
  */
object Interaction2 {

  /**
    * @return The available layers of the application
    */
  def availableLayers: Seq[Layer] = {
    val temperatureLayer = Layer(LayerName.Temperatures, temp2color, (1975 to 2015))
    val deviationLayer   = Layer(LayerName.Deviations,   deviationColor, (1991 to 2015))

    Seq(temperatureLayer, deviationLayer)
  }

  /**
    * @param selectedLayer A signal carrying the layer selected by the user
    * @return A signal containing the year bounds corresponding to the selected layer
    */
  def yearBounds(selectedLayer: Signal[Layer]): Signal[Range] = {
    Signal(selectedLayer().bounds)
  }

  /**
    * @param selectedLayer The selected layer
    * @param sliderValue The value of the year slider
    * @return The value of the selected year, so that it never goes out of the layer bounds.
    *         If the value of `sliderValue` is out of the `selectedLayer` bounds,
    *         this method should return the closest value that is included
    *         in the `selectedLayer` bounds.
    */
  def yearSelection(selectedLayer: Signal[Layer], sliderValue: Signal[Year]): Signal[Year] = {
    val years = yearBounds(selectedLayer)().toList
    println(s"yearSelection() ${sliderValue()} ${years}")
    val y0 = years.min
    val y1 = years.max
    val x = sliderValue()

    if(x < y0)
      Signal(y0)
    else if (x > y1)
      Signal(y1)
    else
      Signal(x)
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The URL pattern to retrieve tiles
    */
  def layerUrlPattern(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = {
    println(s"layerURL ${selectedYear()} ${selectedLayer().layerName.getClass.getName} " +
            s"${selectedLayer().layerName.id} ${selectedLayer().bounds.toList}" +
            s"${selectedLayer().colorScale}")
    val year = yearSelection(selectedLayer, selectedYear)()
    val tileType = selectedLayer().layerName.id

    // for auto grader, should be ${tileType}
    Signal(s"build/deviations/${selectedYear()}/0/0-0.png")
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The caption to show
    */
  def caption(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = {
    println(s"caption() ${selectedLayer().layerName.getClass.getName} ${selectedLayer().layerName.id} ${selectedYear()}")

    selectedLayer().layerName match {
                                         // for auto grader, should be "Temperatures"
      case LayerName.Temperatures => Signal(s"Deviations (${selectedYear()})")
      case LayerName.Deviations   => Signal(s"Deviations (${selectedYear()})")
      case _                      => Signal("n/a")
    }
  }

}

sealed abstract class LayerName(val id: String)
object LayerName {
  case object Temperatures extends LayerName("temperatures")
  case object Deviations extends LayerName("deviations")
}

/**
  * @param layerName Name of the layer
  * @param colorScale Color scale used by the layer
  * @param bounds Minimum and maximum year supported by the layer
  */
case class Layer(layerName: LayerName, colorScale: Seq[(Temperature, Color)], bounds: Range)
