package com.gopetracca.logic

import com.gopetracca.Tags
import com.gopetracca.entities.{Consents, Users}
import com.gopetracca.repositories._
import com.holdenkarau.spark.testing.ScalaDataFrameSuiteBase
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Tag


class UseCase1Test extends AnyFunSuite with GivenWhenThen with ScalaDataFrameSuiteBase {

  import spark.implicits._

  test("test FilterByAge method", Tags.UnitTest) {
    Given("A DataFrame with two Users, only one is older than 18")
      val users = Seq(
        Users("John", 4, "Madrid"),
        Users("Jane", 20, "Paris")
      ).toDF()

      // Inject dependencies
      val userLoader = new FakeReadRepository(users)
      val consentLoader = new FakeReadRepository(spark.emptyDataFrame)
      val writer = new FakeWriteRepository
      val useCase = new UseCase1(userLoader, consentLoader, writer)

    When("filter by age>18")
      val expected = Seq(Users("Jane", 20, "Paris")).toDF()
      val result = useCase.filterByAge(users, "age", 18)

    Then("Only one User is left")
      assertDataFrameEquals(result, expected)
  }

  test("test FilterByConsent method", Tags.UnitTest) {

    Given("A DataFrame with two Users where only one gave consent")
      val consents = Seq(
        Consents("John", true),
        Consents("Jane", false)
      ).toDF()

      // Inject dependencies
      val userLoader = new FakeReadRepository(spark.emptyDataFrame)
      val consentLoader = new FakeReadRepository(consents)
      val writer = new FakeWriteRepository
      val useCase = new UseCase1(userLoader, consentLoader, writer)

    When("filter by consent==true")
      val expected = Seq(Consents("John", true)).toDF()
      val result = useCase.filterByConsent(consents, "consent")

    Then("Only the User with consent==true is left")
      assertDataFrameEquals(result, expected)
  }
}
