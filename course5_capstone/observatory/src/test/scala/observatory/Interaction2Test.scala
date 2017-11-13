package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import Interaction2._

trait Interaction2Test extends FunSuite with Checkers {
  val tempLayer = Signal(Layer(LayerName.Temperatures, Seq.empty, (1991 to 2015)))

  test("yearBounds"){
    val years = yearBounds(tempLayer)
    assert(years() === (1991 to 2015))
  }

  test("yearSelection"){
    assert(yearSelection(tempLayer, Signal(1990))() == 1991)
    assert(yearSelection(tempLayer, Signal(1991))() == 1991)
    assert(yearSelection(tempLayer, Signal(1992))() == 1992)
    assert(yearSelection(tempLayer, Signal(2015))() == 2015)
    assert(yearSelection(tempLayer, Signal(2016))() == 2015)
  }
}
