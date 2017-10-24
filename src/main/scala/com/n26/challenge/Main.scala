package com.n26.challenge

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future

object Main {
  def main(args: Array[String]): Unit = {
    val dummyHandler = new Service[http.Request, http.Response] {
      override def apply(request: Request): Future[Response] = {
        Future.value(
          http.Response(request.version, Status.Ok)
        )
      }
    }

    val httpServer = new HttpServer(dummyHandler, dummyHandler)

    httpServer.start()
    httpServer.join()
  }
}
