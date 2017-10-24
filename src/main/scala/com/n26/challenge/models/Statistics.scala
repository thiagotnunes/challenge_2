package com.n26.challenge.models

import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._

case class Statistics(sum: Double,
                      avg: Double,
                      max: Double,
                      min: Double,
                      count: Long)

object Statistics {
  val Empty = Statistics(0, 0, Double.MinValue, Double.MaxValue, 0)

  implicit val writes: Writes[Statistics] = (
    (JsPath \ "sum").write[Double] and
      (JsPath \ "avg").write[Double] and
      (JsPath \ "max").write[Double] and
      (JsPath \ "min").write[Double] and
      (JsPath \ "count").write[Long]
  )(unlift(Statistics.unapply))
}