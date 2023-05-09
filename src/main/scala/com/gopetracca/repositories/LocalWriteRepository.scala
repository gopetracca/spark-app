package com.gopetracca.repositories

import com.gopetracca.logic.WriteRepositoryInt
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, DataFrameWriterV2, Row, SparkSession}


class LocalWriteRepository(
                           path: String,
                           format: String,
                           options: Array[(String, String)]
  ) extends WriteRepositoryInt {

  def write(df: DataFrame): DataFrame = {
    val writerBase: DataFrameWriter[Row] = df.write.format(format)
    // Apply the options to the writer
    val writer = options.foldLeft(writerBase)((r, opt) => r.option(opt._1, opt._2))
    // Save the data
    writer.mode("overwrite").save(path)
    df
  }
}

object LocalWriteRepository {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.SparkSession
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
      .config("spark.eventLog.enabled", "true")
      // TODO: The directory must already exist!
      .config("spark.eventLog.dir", "file:///Users/gopetracca/projects/scala-app/tmp/spark/logs")
    val spark = sparkBuilder.getOrCreate()

    // Create dummy df
    val df = spark.range(5).toDF()
    // Set path to save the data
    val path = "file:///Users/gopetracca/projects/scala-app/test_writer.csv"
    val options_csv: Array[(String,String)] = Array(("delimiter", ";"), ("inferSchema", "true"), ("header", "true"))
    //val options_json: Array[(String,String)] = Array(("multiline", "true"), ("inferSchema", "true"))
    val options = options_csv
    val format = "csv"
    val dataWriter = new LocalWriteRepository(path, format, options)

    dataWriter.write(df)

  }
}