package com.n26.challenge

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}

import com.n26.challenge.models.Statistics
import com.n26.challenge.repositories.TransactionsRepository

import scala.collection.JavaConverters._

class StatisticsCalculator(clock: Clock, repository: TransactionsRepository) {
  def calculate(): Statistics = {
    val expirationTime = Instant.now(clock).minus(60, ChronoUnit.SECONDS).toEpochMilli

    repository
      .findAll()
      .asScala
      .filter(_.timestamp >= expirationTime)
      .foldLeft(Statistics.Empty)((acc, transaction) => {
        val sum = acc.sum + transaction.amount
        val count = acc.count + 1
        val avg = sum / count
        val max = Math.max(acc.max, transaction.amount)
        val min = Math.min(acc.min, transaction.amount)

        Statistics(sum, avg, max, min, count)
      })
  }
}
