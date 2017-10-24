package com.n26.challenge

import com.n26.challenge.models.Statistics
import com.n26.challenge.repositories.TransactionsRepository

import scala.collection.JavaConverters._

class StatisticsCalculator(expirationChecker: ExpirationChecker,
                           repository: TransactionsRepository) {
  def calculate(): Statistics = {
    repository
      .findAll()
      .asScala
      .filter(transaction => expirationChecker.isNotExpired(transaction.timestamp))
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
