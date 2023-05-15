package com.gopetracca

import com.gopetracca.logic.UseCase1
import com.gopetracca.repositories.{FakeWriteRepository, LocalReadRepository}
import org.apache.spark.sql.SparkSession

/**
 * @author ${user.name}
 */

object SparkTest {
  def apply(): SparkSession = {
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
      // Logging config
      // TODO: The directory must already exist!
      //.config("spark.eventLog.enabled", "true")
      //.config("spark.eventLog.dir", "tmp/spark/logs")

    sparkBuilder.getOrCreate()
  }
}

object App {

  def main(args : Array[String]) {

    // parse arguments
    val context = "local"
    val useCaseParam = "useCase1"

    // TODO: Get config using factory
    //val config = ConfigFactory(context)

    // Initialize dependencies
    // TODO: Dependencies will depend on config values
    val spark = SparkTest()
    val userLoader = new LocalReadRepository(
      spark = spark,
      path = "src/test/resources/data/release-X_Y_Z/user/table_user.csv",
      options = Array(("header", "true"), ("inferSchema", "true")),
      format = "csv"
    )

    val consentLoader = new LocalReadRepository(
      spark = spark,
      path = "src/test/resources/data/release-X_Y_Z/consent/table_consent.csv",
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
    useCase.main().show()

  }

}
