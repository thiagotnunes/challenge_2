package com.n26.challenge

import java.net.{InetSocketAddress, Socket}

import com.twitter.finagle.Http
import com.twitter.finagle.http.{RequestBuilder, Response}
import com.twitter.io.Buf
import com.twitter.util.Future

import scala.util.Try

class HttpClient(host: String, port: Int) {
  private val http = Http.client.newService(s"$host:$port")

  def post(path: String, body: String): Future[Response] = {
    http(
      RequestBuilder()
        .url(s"http://$host:$port/$path")
        .buildPost(Buf.ByteArray(body.getBytes("UTF-8"): _*))
    )
  }

  def isServiceAvailable(): Boolean = {
    val socket = new Socket()
    Try(socket.connect(new InetSocketAddress(host, port), 1000))
      .flatMap(_ => Try(socket.close()))
      .fold(_ => false, _ => true)
  }
}
