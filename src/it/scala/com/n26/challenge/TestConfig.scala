package com.n26.challenge

import java.time.Clock

import com.n26.challenge.config.AppConfig
import com.twitter.util.Duration

class TestConfig(override val clock: Clock) extends AppConfig {
  override def port = 8080

  override def schedulerIntervalMillis = 10

  override def transactionTtl = Duration.fromSeconds(60)
}
