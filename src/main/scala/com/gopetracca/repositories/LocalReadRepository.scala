package com.gopetracca.repositories

import com.gopetracca.logic.ReadRepositoryInt
import org.apache.spark.sql.{DataFrame, DataFrameReader, SparkSession}


class LocalReadRepository(
                           spark: SparkSession,
                           path: String,
                           format: String,
                           options: Array[(String, String)]
  ) extends ReadRepositoryInt {
  def load(): DataFrame = {
    val readerBase: DataFrameReader = spark.read
    // Apply the options to the reader
    val reader: DataFrameReader = options.foldLeft(readerBase: DataFrameReader)((r, opt) => r.option(opt._1, opt._2))
    // Read the data
    reader.format(format).load(path)
  }
}

object LocalReadRepository {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.SparkSession
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
    val spark = sparkBuilder.getOrCreate()

    //val path = "file:///Users/gopetracca/projects/scala-app/pureconfig.json"
    val path = "file:///Users/gopetracca/projects/scala-app/test2.csv"
    val options_csv: Array[(String,String)] = Array(("delimiter", ";"), ("inferSchema", "true"), ("header", "true"))
    val options_json: Array[(String,String)] = Array(("multiline", "true"), ("inferSchema", "true"))
    val options = options_csv
    val format = "csv"
    val dataLoader = new LocalReadRepository(spark, path = path, format, options)
    val data = dataLoader.load()
    data.show()


  }
}