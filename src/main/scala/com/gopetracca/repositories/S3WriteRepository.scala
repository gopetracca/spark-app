package com.gopetracca.repositories

import com.gopetracca.logic.WriteRepositoryInt
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row, SparkSession}


class S3WriteRepository(
                           spark: SparkSession,
                           full_path: String,
                           format: String,
                           options: Array[(String, String)]
  ) extends WriteRepositoryInt {

/*  // Auxiliary constructor
  def this(
            spark: SparkSession,
            bucket: String,
            path: String,
            format: String,
            options: Array[(String, String)]
  ) = {
    this(spark, s"s3a://$bucket/$path", format, options)
  }*/

  def write(df: DataFrame): DataFrame = {
    val writerBase: DataFrameWriter[Row] = df.write.format(format)
    // Apply the options to the writer
    val writer = options.foldLeft(writerBase)((r, opt) => r.option(opt._1, opt._2))
    // Save the data
    writer.mode("overwrite").save(full_path)
    df
  }

}

object S3WriteRepository {
  def main(args: Array[String]): Unit = {
    // Example usage
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
    val path = "output/user_consent"
    val options_csv: Array[(String,String)] = Array(("delimiter", ","), ("header", "true"))
    val options = options_csv
    val format = "csv"
    val data = spark.range(4).toDF()
    val dataWriter = new S3WriteRepository(spark, s"s3a://$bucket/$path", format, options)
    val result = dataWriter.write(data)

  }
}