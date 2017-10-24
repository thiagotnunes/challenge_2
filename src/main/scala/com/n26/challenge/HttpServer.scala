package com.n26.challenge

import java.net.InetSocketAddress

import com.twitter.conversions.time._
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.path.{/, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.Await

class HttpServer(transactionsHandler: Service[http.Request, http.Response],
                 statisticsHandler: Service[http.Request, http.Response]) {

  private var server: Server = _
  private val router = RoutingService.byMethodAndPathObject {
    case (Method.Post, Root / "transactions") => transactionsHandler
    case (Method.Get, Root / "statistics") => transactionsHandler
  }

  def start(): Unit = {
    server = ServerBuilder()
      .stack(Http.server)
      .name("challenge")
      .bindTo(new InetSocketAddress(8080))
      .requestTimeout(30.seconds)
      .build(router)
  }

  def join(): Unit = {
    Await.ready(server)
  }

  def stop(): Unit = {
    server.close()
  }
}
