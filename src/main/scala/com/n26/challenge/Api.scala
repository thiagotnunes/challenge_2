package com.n26.challenge

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Future}
import com.twitter.conversions.time._

object Api {
  def main(args: Array[String]): Unit = {
    val dummyHandler = new Service[http.Request, http.Response] {
      override def apply(request: Request): Future[Response] = {
        Future.value(
          http.Response(request.version, Status.Ok)
        )
      }
    }

    val router = RoutingService.byMethodAndPathObject {
      case (Method.Post, Root / "transactions") => dummyHandler
      case (Method.Get, Root / "statistics") => dummyHandler
    }

    val server = ServerBuilder()
      .stack(Http.server)
      .name("challenge")
      .bindTo(new InetSocketAddress(8080))
      .requestTimeout(30.seconds)
      .build(router)

    Await.ready(server)
  }
}
