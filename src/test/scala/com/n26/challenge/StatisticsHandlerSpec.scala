package com.n26.challenge

import java.time.{Clock, Instant, ZoneOffset}

import com.n26.challenge.JsonMatchers._
import com.n26.challenge.handlers.StatisticsHandler
import com.n26.challenge.models.Transaction
import com.n26.challenge.repositories.TransactionsRepository
import com.twitter.finagle.http.Status
import com.twitter.util.Duration
import org.specs2.mutable.{BeforeAfter, Specification}

class StatisticsHandlerSpec extends Specification {

  sequential

  trait Context extends BeforeAfter {
    private val port = 8080
    val now: Instant = Instant.now()
    val http: HttpClient = new HttpClient("localhost", port)
    val repository: TransactionsRepository = new TransactionsRepository
    private val expirationStrategy = new ExpirationStrategy(Clock.fixed(now, ZoneOffset.UTC), Duration.fromSeconds(60))
    private val calculator = new StatisticsCalculator(expirationStrategy, repository)
    private val statisticsHandler = new StatisticsHandler(calculator)
    private val server = new HttpServer(port, NoOpHandler, statisticsHandler)

    override def before: Any = {
      server.start()
      while (!http.isServiceAvailable()) {
        println("Http service unavailable, waiting for boot up")
        Thread.sleep(1000)
      }
    }

    override def after: Any = {
      server.stop()
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
    repository.insert(Transaction(10, now.minusSeconds(60).toEpochMilli))
    repository.insert(Transaction(20, now.minusSeconds(61).toEpochMilli))
    repository.insert(Transaction(30, now.minusSeconds(10).toEpochMilli))
    repository.insert(Transaction(40, now.minusSeconds(80).toEpochMilli))
    repository.insert(Transaction(50, now.minusSeconds(1).toEpochMilli))

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
