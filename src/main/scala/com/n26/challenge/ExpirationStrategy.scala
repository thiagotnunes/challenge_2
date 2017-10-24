package com.n26.challenge

import java.time.{Clock, Instant}

import com.twitter.util.Duration

class ExpirationStrategy(clock: Clock, expirationTime: Duration) {

  def isNotExpired(timestamp: Long): Boolean = {
    val now = Instant.now(clock)
    val expirationTimestamp = now.minusNanos(expirationTime.inNanoseconds).toEpochMilli

    timestamp >= expirationTimestamp
  }
}
