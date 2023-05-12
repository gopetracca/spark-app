package com.gopetracca.logic

import com.gopetracca.LocalStackContainerForAll
import com.gopetracca.repositories._
import com.holdenkarau.spark.testing.{ScalaDataFrameSuiteBase, SparkContextProvider}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructType}
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite
import org.apache.spark.sql.types.{BooleanType, IntegerType, StructField}


class UseCase1S3IntegrationTest extends AnyFunSuite
  with GivenWhenThen
  with SparkContextProvider
  with ScalaDataFrameSuiteBase
  with LocalStackContainerForAll {

  override def conf: SparkConf = {
    val conf = super.conf
    conf
      .setMaster("local[*]")
      .setAppName("testXXXX")
      .set("spark.ui.enabled", "false")
      .set("spark.app.id", appID)
      .set("spark.driver.host", "localhost")
      // TODO: The directory must already exist!
      .set("spark.eventLog.enabled", "true")
      .set("spark.eventLog.dir", "tmp/spark/logs")
      // Needed for localstack
      .set("spark.hadoop.fs.s3a.endpoint", s"http://localhost:${this.localstackPort}")
      .set("spark.hadoop.fs.s3a.access.key", "FAKE_ACCESS_KEY")
      .set("spark.hadoop.fs.s3a.secret.key", "FAKE_SECRET_KEY")
      .set("spark.hadoop.fs.s3a.path.style.access", "true") // Set path style access
      .set("spark.hadoop.fs.s3a.connection.ssl.enabled", "false") // Disable SSL for Localstack
  }

  test("UseCase1: Return all adult users that gave consent") {
    import spark.implicits._

    Given("A Bucket containing User data where two Users.age > 18")
    val userLoader = new S3ReadRepository(
      spark = spark,
      bucket = "fake-bucket-testing",
      path = "release-X_Y_Z/user/table_user.csv",
      options = Array(("header", "true"), ("inferSchema", "true")),
      format = "csv"
    )

    And("A Bucket containing User data in csv where two Users gave consent")
    val consentLoader = new S3ReadRepository(
      spark = spark,
      full_path = "s3a://fake-bucket-testing/release-X_Y_Z/consent/",
      options = Array(("header", "true"), ("inferSchema", "true")),
      format = "csv"
    )

    val writer = new FakeWriteRepository()

    When("the main method in UseCase is executed")
    // inject dependencies
    val useCase = new UseCase1(userLoader, consentLoader, writer)
    // TODO: Make it easier to build the expected result
    val expectedSchema = StructType(Array(
      StructField("name", StringType, true),
      StructField("age", IntegerType, true),
      StructField("city", StringType, true),
      StructField("consent", BooleanType, true)
    ))

    val expectedRaw = Seq(
      Row("Gabriel", 19, "Madrid", true),
      Row("Susan", 34, "Madrid", true)
    )
    val rdd = sc.parallelize(expectedRaw)
    val expectedDF = spark.createDataFrame(rdd, expectedSchema)
    val res = useCase.main()

    Then("The expected users appear in the result")
    assertDataFrameEquals(res, expectedDF)

  }
}
