package stackoverflow

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File


@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    // testObject.sc.textFile(testObject.path)
  }

  override def afterAll(): Unit = {
    // testObject.sc.stop
  }

  lazy val testObject = new StackOverflow {
    @transient lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("StackOverflow")
    @transient lazy val sc: SparkContext = new SparkContext(conf)

    //val path    = "src/main/resources/stackoverflow/short.csv"
    val path    = "src/main/resources/stackoverflow/stackoverflow.csv"

    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")

    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }


  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }



  test("kmeans ") {
    import testObject._
    val lines   = testObject.sc.textFile(testObject.path)
    val raw     = rawPostings(lines)

    val grouped = groupedPostings(raw)
    for (x <- grouped.collect) {
      //println(x._1, x._2)
    }

    val scored  = scoredPostings(grouped)
    for(x <- scored){
      //println("scoredPostings ", x._1, x._2)
    }

    val vectors = vectorPostings(scored)
    for(x <- vectors){
      //println("vectorPostings ", x._1, x._2)
    }
    println(vectors.count)

    //    assert(vectors.count() == 2121822, "Incorrect number of vectors: " + vectors.count())

    val means   = kmeans(sampleVectors(vectors), vectors, debug = true)
    val results = clusterResults(means, vectors)
    printResults(results)
    testObject.sc.stop
  }

  test("cluster") {
    val vectors = StackOverflow.sc.parallelize(List((550000, 13), (200000, 12), (50000, 16)))
    val means = Array((125000, 14), (550000, 13))
    val results = testObject.clusterResults(means, vectors)
    testObject.printResults(results)

    assert(results.contains("Haskell", 100.0, 1, 13))
    assert(results.contains("C#",       50.0, 2, 14))
  }

  test("cluster2"){
    val QUESTION_TYPE = 1
    val ANSWER_TYPE = 2
    val vectors = StackOverflow.sc.parallelize(
      List(
        Posting(QUESTION_TYPE,1,None,None,0,Some("C#")),
        Posting(QUESTION_TYPE,2,None,None,0,Some("Haskell")),
        Posting(QUESTION_TYPE,3,None,None,0,Some("Java")),
        Posting(ANSWER_TYPE,4,None,Some(1),11,Some("C#")),
        Posting(ANSWER_TYPE,5,None,Some(1),12,Some("C#")),
        Posting(ANSWER_TYPE,6,None,Some(2),13,Some("Haskell")),
        Posting(ANSWER_TYPE,7,None,Some(3),14,Some("Java")),
        Posting(ANSWER_TYPE,8,None,Some(3),15,Some("Java")),
        Posting(ANSWER_TYPE,9,None,Some(3),16,Some("Java"))
      )
    )


  }
}
