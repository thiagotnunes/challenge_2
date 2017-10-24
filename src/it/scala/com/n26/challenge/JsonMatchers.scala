package com.n26.challenge

import org.specs2.matcher.Matcher
import org.specs2.matcher.MatchersImplicits._
import play.api.libs.json.Json

object JsonMatchers {
  def beEqualToJson(expectedJsonString: String): Matcher[String] = { (actualJsonString: String) =>
    val expectedJson = Json.parse(expectedJsonString)
    val actualJson = Json.parse(actualJsonString)

    (
      actualJson == expectedJson,
      s"""
         |json content differs:
         |actual:   ${Json.stringify(actualJson)}
         |expected: ${Json.stringify(expectedJson)}
      """.stripMargin
    )
  }
}
