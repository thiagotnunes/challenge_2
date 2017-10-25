package com.n26.challenge.config

import java.time.Clock

import com.twitter.util.Duration

class ProductionConfig extends AppConfig {
  override def port: Int = 8080

  override def clock: Clock = Clock.systemUTC()

  override def schedulerIntervalMillis: Long = 100

  override def transactionTtl: Duration = Duration.fromSeconds(60)
}
