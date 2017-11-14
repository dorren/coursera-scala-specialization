package observatory

import java.nio.file.Paths
import java.time.LocalDate

import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types._

import math.round
import org.apache.log4j.{Level, Logger}
/**
  * 1st milestone: data extraction
  */
object Extraction {
  //Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Weather")
      .config("spark.master", "local")
      .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._
  spark.sparkContext.setLogLevel("ERROR")


  /**
    * convert farenheit temperature to celcius
    *
    * @param farenheit
    * @return
    */
  def tempConvert(farenheit: Double): Temperature = {
    round((farenheit - 32) * 50 / 9) / 10.0
  }

  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString

  def readFile(path: String) = {
    spark.sparkContext.textFile(fsPath(path))
  }

  def stationSchema(): StructType = {
    StructType(
      Seq(
        StructField("stn",  StringType, false),
        StructField("wban", StringType, true),
        StructField("lat", DoubleType, false),
        StructField("lon", DoubleType, false)
      )
    )
  }

  def temperatureSchema(): StructType = {
    StructType(
      Seq(
        StructField("stn",  StringType, false),
        StructField("wban", StringType, true),
        StructField("month", IntegerType, false),
        StructField("day",   IntegerType, false),
        StructField("farenheit", DoubleType, false)
      )
    )
  }

  def stationDF(stationsFile: String): DataFrame = {
    val stationRDD = readFile(stationsFile)

    val data = stationRDD.map(_.split(",").to[List])
      .filter(x => x.length == 4)
      .map(x => Row.fromSeq(Seq(x(0), x(1), x(2).toDouble, x(3).toDouble)))

    spark.createDataFrame(data, stationSchema)
  }

  def temperatureDF(temperaturesFile: String): DataFrame = {
    val stationRDD = readFile(temperaturesFile)
    val data = stationRDD.map(_.split(",").to[List]).filter(_.length == 5)
      .map(x => Row.fromSeq(Seq(x(0), x(1), x(2).toInt, x(3).toInt, x(4).toDouble)))

    spark.createDataFrame(data, temperatureSchema)
  }

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year,
                         stationsFile: String,
                         temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    val stnDF = stationDF(stationsFile)
    val tempDF = temperatureDF(temperaturesFile)

    val cond = tempDF("stn") === stnDF("stn") && tempDF("wban") === stnDF("wban")
    val joined = tempDF.join(stnDF, cond, "inner")
                   .select(tempDF("stn"), tempDF("wban"), $"month", $"day", stnDF("lat"), stnDF("lon"), $"farenheit")
                   .as[TemperatureRow]

    // the collect part is very costly and could time out spark.
    // therefore yearlyAverageCombined() combines locateTemperatures() and locationYearlyAverageRecords() to one method.
    val result = joined.collect.map(r => (LocalDate.of(year, r.month, r.day),
                             Location(r.lat, r.lon),
                             tempConvert(r.farenheit))
                      )
    result
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]):
        Iterable[(Location, Temperature)] = {
    records.groupBy(x => x._2).mapValues(list => {
      val temps = list.map(_._3)
      1.0 * temps.sum / temps.size
    })
  }

  def yearlyAverageCombined(year: Year,
                            stationsFile: String,
                            temperaturesFile: String): Iterable[(Location, Temperature)] = {
    val stnDF = stationDF(stationsFile)
    val tempDF = temperatureDF(temperaturesFile)
    import org.apache.spark.sql.expressions.scalalang.typed

    val result = tempDF.join(stnDF, tempDF("stn") === stnDF("stn") && tempDF("wban") === stnDF("wban"), "inner")
      .select(tempDF("stn"), tempDF("wban"), $"month", $"day", stnDF("lat"), stnDF("lon"), $"farenheit")
      .as[TemperatureRow]
      .map(r => (Location(r.lat, r.lon),
                 tempConvert(r.farenheit))
      ).groupByKey(_._1)
      .agg(
        typed.avg(_._2)
      ).collect


    result
  }

}

case class TemperatureRow(
             stn: String,
             wban: String,
             month: Integer,
             day: Integer,
             lat: Double,
             lon: Double,
             farenheit: Double
           )