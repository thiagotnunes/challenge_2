package com.n26.challenge.parsers

import java.time.temporal.ChronoUnit.SECONDS
import java.time.{Clock, Instant}

import com.n26.challenge.handlers.ApiError
import com.n26.challenge.models.Transaction
import play.api.libs.json.{JsValue, Json}

import scala.util.Try

class TransactionParser(clock: Clock) {
  def parse(content: String): Either[ApiError, Transaction] = {
    for {
      json <- toJson(content)
      transaction <- asTransaction(json)
      _ <- atMost60SecondsOld(transaction)
    } yield {
      transaction
    }
  }

  private def toJson(content: String): Either[ApiError, JsValue] = {
    Try(Json.parse(content))
      .fold(_ => Left(ApiError.MalformedJson), Right(_))
  }

  private def asTransaction(json: JsValue): Either[ApiError, Transaction] = {
    json.validate[Transaction]
      .fold(_ => Left(ApiError.MalformedJson), Right(_))
  }

  private def atMost60SecondsOld(transaction: Transaction): Either[ApiError, Unit] = {
    Either.cond(
      transaction.timestamp >= Instant.now(clock).minus(60, SECONDS).toEpochMilli,
      Unit,
      ApiError.OldTransaction
    )
  }
}