package com.n26.challenge

import com.n26.challenge.handlers.TransactionsHandler
import com.n26.challenge.repositories.TransactionsRepository
import com.twitter.finagle.http.Status
import org.specs2.mutable.{BeforeAfter, Specification}

import scala.collection.JavaConverters._
import scala.collection.mutable

class TransactionsHandlerSpec extends Specification {

  sequential

  trait Context extends BeforeAfter {
    private val port = 8080
    val http: HttpClient = new HttpClient("localhost", port)
    val repository: TransactionsRepository = new TransactionsRepository
    private val transactionsHandler = new TransactionsHandler(repository)
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

  "returns ok response" in new Context {
    private val response = http.postJson("/transactions")(
      """
        |{
        |  "amount": 12.3,
        |  "timestamp": 1478192204000
        |}
      """.stripMargin
    )
    private val transaction = repository.findAll().get(0)

    response.status ==== Status.Created
    transaction.amount must beCloseTo(12.3, 1.significantFigure)
    transaction.timestamp ==== 1478192204000L
  }
}
