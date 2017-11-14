package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Interaction.zoomTile
import Interaction.tileLocation
import Visualization.interpolateColor

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {
  val deviationColor =
    Seq(( 7.0, Color(255,255,255)),
        ( 4.0, Color(255,  0,  0)),
        ( 2.0, Color(255,255,  0)),
        ( 0.0, Color(255,255,255)),
        (-2.0, Color(  0,255,255)),
        (-7.0, Color(  0,  0,255))
      )

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature,
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature = {
    d00 * (1 - point.x) * (1 - point.y) +
    d10 *      point.x  * (1 - point.y) +
    d01 * (1 - point.x) *      point.y  +
    d11 *      point.x  *      point.y
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {
    // grid function should be Manipulation.deviation(), which returns temp diff.
    def generatePixel(grid_fn: GridLocation => Temperature,
                      colors:  Iterable[(Temperature, Color)],
                      tile: Tile): Pixel = {
      val loc = tileLocation(tile)
      val x0 = loc.lon.floor.toInt
      val x1 = loc.lon.ceil.toInt
      val y0 = loc.lat.floor.toInt
      val y1 = loc.lat.ceil.toInt


      val d00 = grid_fn(GridLocation(y0, x0))
      val d01 = grid_fn(GridLocation(y1, x0))
      val d10 = grid_fn(GridLocation(y0, x1))
      val d11 = grid_fn(GridLocation(y1, x1))

      val point = CellPoint(loc.lon - x0, y1 - loc.lat)
      val temp = bilinearInterpolation(point, d00, d01, d10, d11)
      //println(s"genPixel grid ${x0} ${x1} ${y0} ${y1} ${temp}")
      val color = interpolateColor(colors, temp)
      Pixel(color.red, color.green, color.blue, 127)
    }

    val pixels = zoomTile(tile, 7).par.map(tile => {
      generatePixel(grid, colors, tile)
    })

    Image(128, 128, pixels.toArray).scale(2.0)
  }

}
