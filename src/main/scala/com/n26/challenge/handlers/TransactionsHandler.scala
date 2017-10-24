package com.n26.challenge.handlers

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future

class TransactionsHandler extends Service[http.Request, http.Response] {
  override def apply(request: Request): Future[Response] = {
    Future.value(http.Response(request.version, Status.Ok))
  }
}
