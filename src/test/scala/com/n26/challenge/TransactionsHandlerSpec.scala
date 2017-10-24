package com.n26.challenge

import com.n26.challenge.handlers.TransactionsHandler
import com.twitter.finagle.http.Status
import com.twitter.util.Await
import org.specs2.mutable.{BeforeAfter, Specification}

class TransactionsHandlerSpec extends Specification {

  sequential

  trait Context extends BeforeAfter {
    private val transactionsHandler = new TransactionsHandler
    private val port = 8080
    private val server = new HttpServer(port, transactionsHandler, NoOpHandler)
    val http = new HttpClient("localhost", port)

    override def before: Any = {
      server.start()
      while(!http.isServiceAvailable()) {
        println("Http service unavailable, waiting for boot up")
        Thread.sleep(1000)
      }
    }

    override def after: Any = {
      server.stop()
    }
  }

  "returns ok response" in new Context {
    private val response = http.post("/transactions", "")

    Await.result(response).status ==== Status.Ok
  }
}
