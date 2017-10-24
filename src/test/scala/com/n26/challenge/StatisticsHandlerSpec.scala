package com.n26.challenge

import com.n26.challenge.handlers.StatisticsHandler
import com.n26.challenge.repositories.TransactionsRepository
import com.twitter.finagle.http.Status
import org.specs2.mutable.{BeforeAfter, Specification}
import JsonMatchers._
import com.n26.challenge.models.Transaction

class StatisticsHandlerSpec extends Specification {

  sequential

  trait Context extends BeforeAfter {
    private val port = 8080
    val http: HttpClient = new HttpClient("localhost", port)
    val repository: TransactionsRepository = new TransactionsRepository
    private val calculator = new StatisticsCalculator(repository)
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

  "returns stats from the transactions in the repository" in new Context {
    repository.insert(Transaction(10, 0L))
    repository.insert(Transaction(20, 0L))
    repository.insert(Transaction(30, 0L))
    repository.insert(Transaction(40, 0L))
    repository.insert(Transaction(50, 0L))

    private val response = http.getJson("/statistics")

    response.status ==== Status.Ok
    response.getContentString() must beEqualToJson(
      s"""
         |{
         |  "sum": 150,
         |  "avg": 30,
         |  "max": 50,
         |  "min": 10,
         |  "count": 5
         |}
      """.stripMargin
    )
  }
}
