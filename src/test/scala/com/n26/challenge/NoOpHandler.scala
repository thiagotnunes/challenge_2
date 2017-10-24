package com.n26.challenge

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future

object NoOpHandler extends Service[http.Request, http.Response] {
  override def apply(request: Request): Future[Response] = {
    Future.value(http.Response(Status.InternalServerError))
  }
}
