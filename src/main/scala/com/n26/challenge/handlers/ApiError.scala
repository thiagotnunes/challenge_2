package com.n26.challenge.handlers

sealed trait ApiError

object ApiError {
  case object MalformedJson extends ApiError
  case object ExpiredTransaction extends ApiError
}
