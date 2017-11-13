package observatory

import java.io.File

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Extraction._
import observatory.Visualization._

import math._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile,
    *         as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val divider = pow(2, tile.zoom)
    val lon = -180 + 360.0 * tile.x / divider
    val lat = atan(sinh(Pi - 2.0 * Pi * tile.y / divider)).toDegrees

    Location(lat, lon)
  }

  /**
    * recursively zoom in and expand tile set.
    *
    * @param tile  starting tile
    * @param depth zoom levels
    * @return array of zoomed in tiles
    */
  def zoomTile(tile: Tile, depth: Int): Iterable[Tile] = {
    def zoom(tiles: Iterable[Tile], depth: Int): Iterable[Tile] = {
      if(depth == 0)
        tiles
      else{
        zoom(tiles.flatMap(tile => {
          Seq(
            Tile(2 * tile.x,     2 * tile.y,     tile.zoom + 1),
            Tile(2 * tile.x + 1, 2 * tile.y,     tile.zoom + 1),
            Tile(2 * tile.x,     2 * tile.y + 1, tile.zoom + 1),
            Tile(2 * tile.x + 1, 2 * tile.y + 1, tile.zoom + 1)
          )
        }), depth - 1)
      }
    }

    zoom(Seq(tile), depth).toArray.sortBy(t => (t.y, t.x))
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)],
           colors:       Iterable[(Temperature, Color)],
           tile: Tile): Image = {
    def generatePixel(temperatures: Iterable[(Location, Temperature)],
                      colors:       Iterable[(Temperature, Color)],
                      tile: Tile): Pixel = {
      val loc = tileLocation(tile)
      val temp = predictTemperature(temperatures, loc)
      val color = interpolateColor(colors, temp)
      Pixel(color.red, color.green, color.blue, 127)
    }

    val pixels = zoomTile(tile, 7).par.map(tile => {
      generatePixel(temperatures, colors, tile)
    })

    Image(128, 128, pixels.toArray).scale(2.0)
  }

  def imagePath(year:Int, t: Tile): String =
    s"target/temperatures/${year}/${t.zoom}/${t.x}-${t.y}.png"

  def generateImage(year: Int, t: Tile, temperatures: Iterable[(Location, Temperature)]): Unit = {
    val path = imagePath(year, t)
    val file = new File(path)

    if(!file.exists) {
      val image = tile(temperatures, temp2color, t)

      pathMkdir(path)
      image.output(new File(path))
      println("generated " + path)
    }else{
      println(s"${path} exists, skip.")
    }
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {

    yearlyData.foreach(x => {
      val (year, avgTemps) = x

      val tiles = (
        for{ i <- 0 to 3
        } yield {
          zoomTile(Tile(0, 0, 0), i)
        }
      ).flatten

      tiles.foreach(t => generateImage(year, t, avgTemps))
    })
  }

}
