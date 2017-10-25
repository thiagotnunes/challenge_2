package com.n26.challenge.handlers

sealed trait ApiError

object ApiError {

  case object InvalidTransaction extends ApiError

  case object MalformedJson extends ApiError

  case object ExpiredTransaction extends ApiError

}
