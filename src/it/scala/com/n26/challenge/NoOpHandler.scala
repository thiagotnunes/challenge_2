package com.n26.challenge

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

object NoOpHandler extends Service[Request, Response] {
  override def apply(request: Request): Future[Response] = {
    Future.value(Response(Status.InternalServerError))
  }
}
