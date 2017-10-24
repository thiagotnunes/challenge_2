package com.n26.challenge.handlers

import com.n26.challenge.models.Transaction
import com.n26.challenge.repositories.TransactionsRepository
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future
import play.api.libs.json.Json

class TransactionsHandler(repository: TransactionsRepository)
  extends Service[http.Request, http.Response] {

  override def apply(request: Request): Future[Response] = {
    val body = request.getContentString()
    val transaction = Json.parse(body).as[Transaction]
    repository.insert(transaction)
    Future.value(http.Response(request.version, Status.Created))
  }

}
