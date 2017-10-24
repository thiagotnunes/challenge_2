package com.n26.challenge.repositories

import java.util
import java.util.Collections

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.models.{Statistics, Transaction}

import scala.collection.JavaConverters._

class StatisticsRepository(expirationChecker: ExpirationChecker) {
  private val transactions = Collections.synchronizedList(new util.ArrayList[Transaction]())
  private var statistics = Statistics.Empty

  def add(transaction: Transaction): Unit = {
    transactions.add(transaction)
    recalculate()
  }

  def findAll(): util.List[Transaction] = {
    transactions
  }

  def getStatistics(): Statistics = {
    this.synchronized {
      statistics.copy()
    }
  }

  def recalculate(): Unit = {
    val newStats = transactions
      .asScala
      .filter(t => expirationChecker.isNotExpired(t.timestamp))
      .foldLeft(Statistics.Empty)((acc, transaction) => {
        val sum = acc.sum + transaction.amount
        val count = acc.count + 1
        val avg = sum / count
        val max = Math.max(acc.max, transaction.amount)
        val min = Math.min(acc.min, transaction.amount)

        Statistics(sum, avg, max, min, count)
      })

    this.synchronized {
      statistics = newStats
    }
  }
}
