package com.n26.challenge.config

import java.time.Clock

import com.twitter.util.Duration

trait AppConfig {
  def port: Int

  def clock: Clock

  def schedulerIntervalMillis: Long

  def transactionTtl: Duration
}
