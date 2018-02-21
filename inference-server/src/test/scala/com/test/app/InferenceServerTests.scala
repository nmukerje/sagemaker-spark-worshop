package com.test.app

import org.scalatra.test.scalatest._

class InferenceServerTests extends ScalatraFunSuite {

  addServlet(classOf[InferenceServer], "/*")

  test("GET /ping on InferenceServer should return status 200"){
    get("/ping"){
      status should equal (200)
    }
  }

}
