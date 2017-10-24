package com.n26.challenge.handlers

import java.time.temporal.ChronoUnit.{MILLIS, SECONDS}
import java.time.{Clock, Instant, ZoneOffset}

import com.n26.challenge.parsers.TransactionParser
import com.n26.challenge.repositories.TransactionsRepository
import com.n26.challenge.{ExpirationChecker, HttpClient, HttpServer, NoOpHandler}
import com.twitter.finagle.http.Status
import com.twitter.util.Duration
import org.specs2.mutable.{BeforeAfter, Specification}

class TransactionsHandlerSpec extends Specification {

  sequential

  trait Context extends BeforeAfter {
    private val port = 8080
    val now: Instant = Instant.now()
    val http: HttpClient = new HttpClient("localhost", port)
    val repository: TransactionsRepository = new TransactionsRepository
    private val expirationChecker = new ExpirationChecker(Clock.fixed(now, ZoneOffset.UTC), Duration.fromSeconds(60))
    private val parser = new TransactionParser(expirationChecker)
    private val transactionsHandler = new TransactionsHandler(parser, repository)
    private val server = new HttpServer(port, transactionsHandler, NoOpHandler)

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

  "returns created when transaction is at most 60 seconds old" in new Context {
    private val timestamp = now.minus(60, SECONDS).toEpochMilli
    private val response = http.postJson("/transactions")(
      s"""
         |{
         |  "amount": 12.3,
         |  "timestamp": $timestamp
         |}
      """.stripMargin
    )
    private val transaction = repository.findAll().get(0)

    response.status ==== Status.Created
    transaction.amount must beCloseTo(12.3, 1.significantFigure)
    transaction.timestamp ==== timestamp
  }

  "returns no content when transaction is older than 60 seconds" in new Context {
    private val response = http.postJson("/transactions")(
      s"""
         |{
         |  "amount": 1,
         |  "timestamp": ${now.minus(60001, MILLIS).toEpochMilli}
         |}
      """.stripMargin
    )

    response.status ==== Status.NoContent
    repository.findAll().size() ==== 0
  }

  "returns bad request when json is malformed" in new Context {
    private val response = http.postJson("/transactions")("{ i-am-malformed: }")

    response.status ==== Status.BadRequest
  }
}
