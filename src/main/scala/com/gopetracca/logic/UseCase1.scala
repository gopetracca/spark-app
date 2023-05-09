package com.gopetracca.logic

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col

class UseCase1 (
               userLoader: ReadRepositoryInt,
               consentLoader: ReadRepositoryInt,
               writer: WriteRepositoryInt,
               ) {
  def main(): DataFrame = {
    println("Running Use Case 1")
    // Load user data
    val user = userLoader.load().cache()
    val userAdult = filterByAge(user, "age", 18)

    // Load consent data
    val consent = consentLoader.load().cache()
    val consentTrue = filterByConsent(consent, "consent")

    // Join user and consent by name
    val userAdultConsent = joinUserConsent(userAdult, consentTrue, usingColumn = "name", joinType = "inner")

    // Write data
    writer.write(userAdultConsent)

  }

  def joinUserConsent(left: DataFrame, right: DataFrame, usingColumn: String, joinType: String): DataFrame = {
    left.join(right, usingColumn = "name")
  }

  def filterByAge(df: DataFrame, col_name: String, age: Int): DataFrame = {
    df.filter(col(col_name) >= age)
  }

  def filterByConsent(df: DataFrame, col_name: String): DataFrame = {
    df.filter(col(col_name) === true)
  }


}
