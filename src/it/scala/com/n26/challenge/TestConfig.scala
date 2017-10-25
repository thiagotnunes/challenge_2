package com.n26.challenge

import java.time.Clock

import com.n26.challenge.config.AppConfig
import com.twitter.util.Duration

class TestConfig(override val clock: Clock) extends AppConfig {
  override def port: Int = 4567

  override def schedulerIntervalMillis: Long = 10

  override def transactionTtl: Duration = Duration.fromSeconds(60)
}
