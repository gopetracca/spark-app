package com.gopetracca.repositories

import com.gopetracca.logic.ReadRepositoryInt
import org.apache.spark.sql.DataFrame


class FakeReadRepository(data: DataFrame) extends ReadRepositoryInt {
  def load(): DataFrame = {
    // Return the DataFrame
    data
  }
}

object FakeReadRepository {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.SparkSession
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
    val spark = sparkBuilder.getOrCreate()

    val df = spark.range(5).toDF()

    val dataLoader = new FakeReadRepository(df)
    val data = dataLoader.load()
    data.show()

  }
}