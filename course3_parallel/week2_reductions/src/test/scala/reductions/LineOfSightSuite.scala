package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory

@RunWith(classOf[JUnitRunner]) 
class LineOfSightSuite extends FunSuite {
  import LineOfSight._
  test("lineOfSight should correctly handle an array of size 4") {
    val output = new Array[Float](4)
    lineOfSight(Array[Float](0f, 1f, 8f, 9f), output)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }


  test("upsweepSequential should correctly handle the chunk 1 until 4 of an array of 4 elements") {
    val res = upsweepSequential(Array[Float](0f, 1f, 8f, 9f), 1, 4)
    assert(res == 4f)
  }


  test("downsweepSequential should correctly handle a 4 element array when the starting angle is zero") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f), output, 0f, 1, 4)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  test("upsweep"){
    val input = List(0.0f, 7.0f, 14.0f, 33.0f, 48.0f).toArray
    //println(input.toList)
    val t = upsweep(input, 0, 5, 1)
    //println(t)
  }

  test("downsweep threshold 1"){
    val input = List(0.0f, 7.0f, 10.0f, 33.0f, 48.0f).toArray
    val output = new Array[Float](input.length)
    parLineOfSight(input, output, 1)
    assert(output.toList == List(0.0, 7.0, 7.0, 11.0, 12.0))
  }

  test("downsweep threshold 2"){
    val input = List(0.0f, 7.0f, 10.0f, 33.0f, 48.0f).toArray
    val output = new Array[Float](input.length)
    parLineOfSight(input, output, 2)
    assert(output.toList == List(0.0, 7.0, 7.0, 11.0, 12.0))
  }

  test("downsweep threshold 5"){
    val input = List(0.0f, 7.0f, 10.0f, 33.0f, 48.0f).toArray
    val output = new Array[Float](input.length)
    parLineOfSight(input, output, 5)
    assert(output.toList == List(0.0, 7.0, 7.0, 11.0, 12.0))
  }
}

