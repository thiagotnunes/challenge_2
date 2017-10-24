package com.n26.challenge

import com.twitter.finagle.Http
import com.twitter.finagle.http.{RequestBuilder, Response}
import com.twitter.io.Buf
import com.twitter.util.Future

class HttpClient(host: String, port: Int) {
  private val http = Http.client.newService(s"$host:$port")

  def post(path: String, body: String): Future[Response] = {
    http(
      RequestBuilder()
        .url(s"http://$host:$port/$path")
        .buildPost(Buf.ByteArray(body.getBytes("UTF-8"): _*))
    )
  }
}
