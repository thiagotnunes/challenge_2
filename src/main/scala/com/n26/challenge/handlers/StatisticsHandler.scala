package com.n26.challenge.handlers

import com.n26.challenge.models.Statistics
import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import play.api.libs.json.Json

class StatisticsHandler(repository: StatisticsRepository) extends Service[Request, Response] {
  override def apply(request: Request): Future[Response] = {
    Future.value(jsonResponseFrom(repository.getStatistics()))
  }

  private def jsonResponseFrom(stats: Statistics): Response = {
    val response = Response(Status.Ok)
    response.contentString = Json.stringify(Json.toJson(stats))
    response.contentType = "application/json"
    response
  }
}
