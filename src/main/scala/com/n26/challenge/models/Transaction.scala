package com.n26.challenge.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class Transaction(amount: Double, timestamp: Long)

object Transaction {
  implicit val reads: Reads[Transaction] = (
    (JsPath \ "amount").read[Double](Reads.verifying[Double](_ > 0)) and
      (JsPath \ "timestamp").read[Long]
    ) (Transaction.apply _)
}
