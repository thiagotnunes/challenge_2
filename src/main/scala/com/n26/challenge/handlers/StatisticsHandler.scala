package com.n26.challenge.handlers

import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import play.api.libs.json.Json

class StatisticsHandler(repository: StatisticsRepository) extends Service[Request, Response] {
  override def apply(request: Request): Future[Response] = {
    val stats = repository.getStatistics()
    val response = Response(request.version, Status.Ok)
    response.contentString = Json.stringify(Json.toJson(stats))
    response.contentType = "application/json"
    Future.value(response)
  }
}
