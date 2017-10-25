package com.n26.challenge

import java.net.InetSocketAddress

import com.twitter.conversions.time._
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.path.{/, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await

class HttpServer(port: Int,
                 transactionsHandler: Service[Request, Response],
                 statisticsHandler: Service[Request, Response]) {

  private var server: Server = _
  private val router = RoutingService.byMethodAndPathObject {
    case (Method.Post, Root / "transactions") => transactionsHandler
    case (Method.Get, Root / "statistics") => statisticsHandler
  }

  def start(): Unit = {
    server = ServerBuilder()
      .stack(Http.server)
      .name("challenge")
      .bindTo(new InetSocketAddress(port))
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
