package com.n26.challenge

import com.n26.challenge.handlers.TransactionsHandler
import com.twitter.finagle.http.Status
import com.twitter.util.Await
import org.specs2.mutable.{BeforeAfter, Specification}

class TransactionsHandlerSpec extends Specification {

  trait Context extends BeforeAfter {
    private val transactionsHandler = new TransactionsHandler
    private val server = new HttpServer(transactionsHandler, NoOpHandler)
    val http = new HttpClient("localhost", 8080)

    override def before: Any = {
      // FIXME: Possible timing issue here
      server.start()
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
