package com.n26.challenge.handlers

import com.n26.challenge.models.Transaction
import com.n26.challenge.parsers.TransactionRequestParser
import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

class TransactionsHandler(parser: TransactionRequestParser,
                          repository: StatisticsRepository)
  extends Service[Request, Response] {

  override def apply(request: Request): Future[Response] = {
    (for {
      body <- Right(request.getContentString())
      transaction <- parser.parse(body)
      _ <- Right(repository.insert(transaction))
    } yield {
      transaction
    }).fold(handleError, handleSuccess)
  }

  private def handleSuccess(transaction: Transaction): Future[Response] = {
    Future.value(Response(Status.Created))
  }

  private def handleError(error: ApiError): Future[Response] = {
    error match {
      case ApiError.InvalidTransaction => Future.value(Response(Status.UnprocessableEntity))
      case ApiError.MalformedJson => Future.value(Response(Status.BadRequest))
      case ApiError.ExpiredTransaction => Future.value(Response(Status.NoContent))
    }
  }
}
