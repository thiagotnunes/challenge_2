package com.n26.challenge.handlers

import com.n26.challenge.models.Transaction
import com.n26.challenge.repositories.TransactionsRepository
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future
import play.api.libs.json.Json

import scala.util.Try

class TransactionsHandler(repository: TransactionsRepository)
  extends Service[http.Request, http.Response] {

  override def apply(request: Request): Future[Response] = {
    (for {
      body <- Right(request.getContentString())
      transaction <- parseTransaction(body)
      _ <- Right(repository.insert(transaction))
    } yield {
      transaction
    }).fold(handleError, handleSuccess)
  }

  private def parseTransaction(body: String): Either[ApiError, Transaction] = {
    for {
      json <- Try(Json.parse(body))
        .fold(_ => Left(ApiError.MalformedJson), js => Right(js))
      transaction <- json.validate[Transaction]
        .fold(_ => Left(ApiError.MalformedJson), transaction => Right(transaction))
    } yield {
      transaction
    }
  }

  private def handleSuccess(transaction: Transaction): Future[Response] = {
    Future.value(Response(Status.Created))
  }

  private def handleError(error: ApiError): Future[Response] = {
    error match {
      case ApiError.MalformedJson => Future.value(Response(Status.BadRequest))
    }
  }
}
