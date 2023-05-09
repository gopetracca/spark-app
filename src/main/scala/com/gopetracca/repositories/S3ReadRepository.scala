package com.gopetracca.repositories

import com.gopetracca.logic.ReadRepositoryInt
import org.apache.spark.sql.{DataFrame, DataFrameReader, SparkSession}


class S3ReadRepository(
                           spark: SparkSession,
                           full_path: String,
                           format: String,
                           options: Array[(String, String)]
  ) extends ReadRepositoryInt {

  // Auxiliary constructor
  def this(
            spark: SparkSession,
            bucket: String,
            path: String,
            format: String,
            options: Array[(String, String)]
  ) = {
    this(spark, s"s3a://$bucket/$path", format, options)
  }

  def load(): DataFrame = {
    val readerBase: DataFrameReader = spark.read
    // Apply the options to the reader
    val reader: DataFrameReader = options.foldLeft(readerBase: DataFrameReader)((r, opt) => r.option(opt._1, opt._2))
    // Read the data
    reader.format(format).load(full_path)
  }

}

object S3ReadRepository {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.SparkSession
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
      // Needed for localstack
      .config("spark.hadoop.fs.s3a.endpoint", "http://localhost:4566")
      .config("spark.hadoop.fs.s3a.access.key", "FAKE_ACCESS_KEY")
      .config("spark.hadoop.fs.s3a.secret.key", "FAKE_SECRET_KEY")
      .config("spark.hadoop.fs.s3a.path.style.access", "true") // Set path style access
      .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false") // Disable SSL for Localstack

    val spark = sparkBuilder.getOrCreate()

    //val path = "file:///Users/gopetracca/projects/scala-app/pureconfig.json"
    val bucket = "fake-bucket"
    val path = "user/table_user.csv"
    val options_csv: Array[(String,String)] = Array(("delimiter", ","), ("inferSchema", "true"), ("header", "true"))
    val options_json: Array[(String,String)] = Array(("multiline", "true"), ("inferSchema", "true"))
    val options = options_csv
    val format = "csv"
    val dataLoader = new S3ReadRepository(spark, bucket, path = path, format, options)
    val data = dataLoader.load()
    data.show()


  }
}