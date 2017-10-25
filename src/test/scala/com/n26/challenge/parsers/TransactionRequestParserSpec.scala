package com.n26.challenge.parsers

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.handlers.ApiError
import com.n26.challenge.models.Transaction
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class TransactionRequestParserSpec extends Specification with Mockito {
  trait Context extends Scope {
    val expirationChecker: ExpirationChecker = mock[ExpirationChecker]
    val parser: TransactionRequestParser = new TransactionRequestParser(expirationChecker)

    expirationChecker.isNotExpired(Mockito.any[Long]).returns(true)
  }

  "returns transaction when payload is valid" in new Context {
    parser.parse(
      """
        |{
        |  "amount": 12.3,
        |  "timestamp": 1478192204000
        |}
      """.stripMargin
    ) must beRight(Transaction(12.3, 1478192204000L))
  }

  "returns error when json is malformed" in new Context {
    parser.parse("{ i-am-malformed: true }") must beLeft[ApiError](ApiError.MalformedJson)
  }

  "returns error when amount is not a double" in new Context {
    parser.parse(
      """
        |{
        |  "amount": "i am a string",
        |  "timestamp": 1478192204000
        |}
      """.stripMargin
    ) must beLeft[ApiError](ApiError.InvalidTransaction)
  }

  "returns error when amount is negative" in new Context {
    parser.parse(
      """
        |{
        |  "amount": -1,
        |  "timestamp": 1478192204000
        |}
      """.stripMargin
    ) must beLeft[ApiError](ApiError.InvalidTransaction)
  }

  "returns error when timestamp is not a long" in new Context {
    parser.parse(
      """
        |{
        |  "amount": 12.3,
        |  "timestamp": 12.3
        |}
      """.stripMargin
    ) must beLeft[ApiError](ApiError.InvalidTransaction)
  }

  "returns error when transaction is expired" in new Context {
    expirationChecker.isNotExpired(Mockito.any[Long]).returns(false)

    parser.parse(
      """
        |{
        |  "amount": 12.3,
        |  "timestamp": 1478192204000
        |}
      """.stripMargin
    ) must beLeft[ApiError](ApiError.ExpiredTransaction)
  }
}
