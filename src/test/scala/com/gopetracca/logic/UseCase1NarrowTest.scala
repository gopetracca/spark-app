package com.gopetracca.logic

import com.gopetracca.entities.{Consents, Users}
import com.gopetracca.repositories._
import com.holdenkarau.spark.testing.ScalaDataFrameSuiteBase
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite

case class Result(name: String, age: Int, city: String, consent: Boolean)

class UseCase1NarrowTest extends AnyFunSuite with GivenWhenThen with ScalaDataFrameSuiteBase {

  import spark.implicits._

  test("Narrow Integration test for UseCase1. Return all adult users that gave consent.") {
    Given("A DataFrame with two Users.age > 18")
      val users = Seq(
        Users("UserX", 20, "Madrid"),
        Users("UserY", 10, "Paris"),
        Users("UserZ", 30, "London"),
      ).toDF()
    And("A DataFrame with two Users where only UserX gave consent")
      val consents = Seq(
        Consents("UserX", true),
        Consents("UserY", true),
        Consents("UserZ", false),
      ).toDF()

    // inject dependencies
    val userLoader = new FakeReadRepository(users)
    val consentLoader = new FakeReadRepository(consents)
    val writer = new FakeWriteRepository()
    val useCase = new UseCase1(userLoader, consentLoader, writer)

    When("the main method in UseCase is executed")
      val expected = Seq(Result("UserX", 20, "Madrid", true)).toDF()
      val res = useCase.main()

    Then("Only one User is left")
      assertDataFrameEquals(res, expected)
  }
}

