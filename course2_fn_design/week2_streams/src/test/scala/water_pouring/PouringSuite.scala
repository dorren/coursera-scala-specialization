package water_pouring

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import water_pouring._

@RunWith(classOf[JUnitRunner])
class PouringSuite  extends FunSuite {
  test("variables ") {
    val problem = new Pouring(Vector(4,7))
    println("init state: " + problem.initialState)
    println("init path:  " + problem.initialPath)
    println("pathSets " + problem.pathSets)
  }

  test("pathSets") {
    val problem = new Pouring(Vector(1,2))
    val paths =
      for {
        pathSet <- problem.pathSets;
        path <- pathSet
        if false
      } yield {
        println("yield " + path)
        path
      }
    println("paths " + paths)
  }

  test("pouring (4,7) --> 6") {
    val problem = new Pouring(Vector(4,7))
    println(problem.solution(6))
  }

  test("pouring (3,5) --> 4") {
    val problem = new Pouring(Vector(3,5))
    println("solution " + problem.solution(4))
  }

  test("pouring (4, 9, 19) --> 17") {
    val problem = new Pouring(Vector(4, 9, 19))

    println("solution " + problem.solution(17))
  }
}
