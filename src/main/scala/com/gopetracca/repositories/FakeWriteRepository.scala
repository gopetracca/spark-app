package com.gopetracca.repositories

import com.gopetracca.logic.WriteRepositoryInt
import org.apache.spark.sql.DataFrame


class FakeWriteRepository() extends WriteRepositoryInt {
  def write(df: DataFrame): DataFrame = {
    // Return the DataFrame
    df
  }
}

object FakeWriteRepository {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.SparkSession
    val sparkBuilder = SparkSession.builder()
      .appName("testing")
      .master("local[2]")
    val spark = sparkBuilder.getOrCreate()

    val df = spark.range(5).toDF()

    val dataWriter = new FakeWriteRepository()
    val data = dataWriter.write(df)
    data.show()

  }
}