package com.gopetracca

import com.gopetracca.logic.UseCase1
import com.gopetracca.repositories.{FakeWriteRepository, LocalReadRepository, S3ReadRepository}
import org.apache.spark.sql.SparkSession

/**
 * @author ${user.name}
 */

object S3SparkTest {
  def apply(): SparkSession = {
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
      // Logging config
      // TODO: The directory must already exist!
      .config("spark.eventLog.enabled", "true")
      .config("spark.eventLog.dir", "file:///Users/gopetracca/projects/scala-app/tmp/spark/logs")
      // Needed for localstack
      .config("spark.hadoop.fs.s3a.endpoint", "http://localhost:4566")
      .config("spark.hadoop.fs.s3a.access.key", "FAKE_ACCESS_KEY")
      .config("spark.hadoop.fs.s3a.secret.key", "FAKE_SECRET_KEY")
      .config("spark.hadoop.fs.s3a.path.style.access", "true") // Set path style access
      .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false") // Disable SSL for Localstack


    sparkBuilder.getOrCreate()
  }
}

object S3App {

  def main(args : Array[String]) {

    val context = "local"
    val useCaseParam = "useCase1"


    // Initialize dependencies
    val spark = S3SparkTest()
    val userLoader = new S3ReadRepository(
      spark = spark,
      bucket = "fake-bucket",
      path = "user/table_user.csv",
      options = Array(("header", "true"), ("inferSchema", "true")),
      format = "csv"
    )

    val consentLoader = new S3ReadRepository(
      spark = spark,
      full_path = "s3a://fake-bucket/consent/",
      options = Array(("header", "true"), ("inferSchema", "true")),
      format = "csv"
    )

    val writer = new FakeWriteRepository()

    // Decide which use case and inject dependencies
    val useCase = useCaseParam match {
      case "useCase1" => new UseCase1(userLoader, consentLoader, writer)
      case _ => throw new RuntimeException("Use Case not known")
    }

    // run the use case
    useCase.main()

  }

}
