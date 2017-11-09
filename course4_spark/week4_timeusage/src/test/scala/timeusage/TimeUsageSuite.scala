package timeusage

import java.io.{BufferedReader, FileReader}

import org.apache.spark.sql.{ColumnName, DataFrame, Row}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import timeusage.TimeUsage._

@RunWith(classOf[JUnitRunner])
class TimeUsageSuite extends FunSuite with BeforeAndAfterAll {

  test("read") {
    val (headers, df) = read("timeusage/atussum.csv")
    // println(headers)

    val data = df.take(1).toList
    // println("data", data)

    // println("classifiedColumns", classifiedColumns(headers))
  }

  test("timeUsageGroupedSql") {

  }
}
