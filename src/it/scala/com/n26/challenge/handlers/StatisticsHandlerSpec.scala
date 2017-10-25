package com.n26.challenge.handlers

import java.time.{Clock, Instant, ZoneOffset}

import com.n26.challenge.JsonMatchers._
import com.n26.challenge._
import com.n26.challenge.config.AppConfig
import com.n26.challenge.models.Transaction
import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.finagle.http.Status
import org.specs2.mutable.{BeforeAfter, Specification}

class StatisticsHandlerSpec extends Specification {

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

  "returns empty stats when no transaction has been created" in new Context {
    private val response = http.getJson("/statistics")

    response.status ==== Status.Ok
    response.getContentString() must beEqualToJson(
      s"""
         |{
         |  "sum": 0,
         |  "avg": 0,
         |  "max": ${Double.MinValue},
         |  "min": ${Double.MaxValue},
         |  "count": 0
         |}
      """.stripMargin
    )
  }

  "returns stats from the transactions from at most 60 seconds ago" in new Context {
    repository.add(Transaction(10, now.minusSeconds(60).toEpochMilli))
    repository.add(Transaction(20, now.minusSeconds(61).toEpochMilli))
    repository.add(Transaction(30, now.minusSeconds(10).toEpochMilli))
    repository.add(Transaction(40, now.minusSeconds(80).toEpochMilli))
    repository.add(Transaction(50, now.minusSeconds(1).toEpochMilli))

    private val response = http.getJson("/statistics")

    response.status ==== Status.Ok
    response.getContentString() must beEqualToJson(
      s"""
         |{
         |  "sum": 90,
         |  "avg": 30,
         |  "max": 50,
         |  "min": 10,
         |  "count": 3
         |}
      """.stripMargin
    )
  }
}
