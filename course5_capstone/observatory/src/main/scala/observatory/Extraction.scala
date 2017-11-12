package observatory

import java.nio.file.Paths
import java.time.LocalDate

import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

import math.round

/**
  * 1st milestone: data extraction
  */
object Extraction {

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Weather")
      .config("spark.master", "local")
      .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._

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

  var _stationDF: DataFrame = null

  def stationDF(stationsFile: String): DataFrame = {
    if(_stationDF == null) {
      val stationRDD = readFile(stationsFile)
      val data = stationRDD.map(_.split(",").to[List])
        .filter(x => x.length == 4 && x(2).toDouble != 0 && x(3).toDouble != 0)
        .map(x => Row.fromSeq(Seq(x(0), x(1), x(2).toDouble, x(3).toDouble)))

      _stationDF = spark.createDataFrame(data, stationSchema)
    }

    _stationDF
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


    val joined = tempDF.join(stnDF, tempDF("stn") === stnDF("stn") && tempDF("wban") === stnDF("wban"), "inner")
                   .select(tempDF("stn"), tempDF("wban"), $"month", $"day", stnDF("lat"), stnDF("lon"), $"farenheit")
                   .as[TemperatureRow]

    joined.collect.map(r => (LocalDate.of(year, r.month, r.day),
                             Location(r.lat, r.lon),
                             tempConvert(r.farenheit))
                      )
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