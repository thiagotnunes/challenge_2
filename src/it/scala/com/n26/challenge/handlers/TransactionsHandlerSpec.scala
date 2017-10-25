package com.n26.challenge.handlers

import java.time.{Clock, Instant, ZoneOffset}

import com.n26.challenge._
import com.n26.challenge.config.AppConfig
import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.finagle.http.Status
import org.specs2.mutable.{BeforeAfter, Specification}

class TransactionsHandlerSpec extends Specification {

  sequential

  trait Context extends BeforeAfter {
    val now: Instant = Instant.now()
    val clock: Clock = Clock.fixed(now, ZoneOffset.UTC)
    val config: AppConfig = new TestConfig(clock)
    val repository: StatisticsRepository = new StatisticsRepository()
    val app: Application = new Application(config, repository)
    val http: HttpClient = new HttpClient("localhost", config.port)

    override def before: Any = {
      app.start()
      while (!http.isServiceAvailable()) {
        println("Http service unavailable, waiting for boot up")
        Thread.sleep(1000)
      }
    }

    override def after: Any = {
      app.stop()
    }
  }

  "returns created when transaction is at most 60 seconds old" in new Context {
    private val timestamp = now.minusSeconds(60).toEpochMilli
    private val response = http.postJson("/transactions")(
      s"""
         |{
         |  "amount": 12.3,
         |  "timestamp": $timestamp
         |}
      """.stripMargin
    )
    private val transaction = repository.findAll()(0)

    response.status ==== Status.Created
    transaction.amount must beCloseTo(12.3, 1.significantFigure)
    transaction.timestamp ==== timestamp
  }

  "returns no content when transaction is older than 60 seconds" in new Context {
    private val response = http.postJson("/transactions")(
      s"""
         |{
         |  "amount": 1,
         |  "timestamp": ${now.minusMillis(60001).toEpochMilli}
         |}
      """.stripMargin
    )

    response.status ==== Status.NoContent
    repository.findAll().length ==== 0
  }

  "returns unprocessable entity when json fields are invalid" in new Context {
    private val response = http.postJson("/transactions")(
      s"""
         |{
         |  "amount": "2",
         |  "timestamp": "2017-01-01"
         |}
      """.stripMargin
    )

    response.status ==== Status.UnprocessableEntity
    repository.findAll().length ==== 0
  }

  "returns bad request when json is malformed" in new Context {
    private val response = http.postJson("/transactions")("{ i-am-malformed: }")

    response.status ==== Status.BadRequest
    repository.findAll().length ==== 0
  }
}
