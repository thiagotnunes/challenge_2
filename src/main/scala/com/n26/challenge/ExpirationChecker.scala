package com.n26.challenge

import java.time.{Clock, Instant}

import com.twitter.util.Duration

class ExpirationChecker(clock: Clock, expirationTime: Duration) {

  def isExpired(timestamp: Long): Boolean = {
    val now = Instant.now(clock)
    val expirationTimestamp = now.minusNanos(expirationTime.inNanoseconds).toEpochMilli

    timestamp < expirationTimestamp
  }

  val isNotExpired: Long => Boolean = !isExpired(_)
}
