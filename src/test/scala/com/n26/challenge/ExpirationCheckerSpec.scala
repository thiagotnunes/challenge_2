package com.n26.challenge

import java.time.{Clock, Instant, ZoneOffset}
import java.util.concurrent.TimeUnit

import com.twitter.util.Duration
import org.specs2.mutable.Specification

class ExpirationCheckerSpec extends Specification {

  private val now = Instant.now()
  private val clock = Clock.fixed(now, ZoneOffset.UTC)
  private val checker = new ExpirationChecker(clock, Duration(60, TimeUnit.SECONDS))

  "returns true when timestamp is no less than 60 seconds from now" in {
    checker.isNotExpired(now.minusSeconds(30).toEpochMilli) ==== true
  }

  "returns true when timestamp is exactly less than 60 seconds from now" in {
    checker.isNotExpired(now.minusSeconds(60).toEpochMilli) ==== true
  }

  "returns false when timestamp is more than 60 seconds from now" in {
    checker.isNotExpired(now.minusMillis(60001).toEpochMilli) ==== false
  }
}
