package com.n26.challenge.repositories

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.models.{Statistics, Transaction}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class StatisticsRepository(expirationChecker: ExpirationChecker) {
  private var transactions = new mutable.ArrayBuffer[Transaction]()
  private var statistics = Statistics.Empty

  def add(transaction: Transaction): Unit = {
    this.synchronized {
      transactions += transaction
      recalculate()
    }
  }

  def findAll(): ArrayBuffer[Transaction] = {
    transactions
  }

  // Time complexity - O(1)
  // Space complexity - O(1)
  def getStatistics(): Statistics = {
    this.synchronized {
      statistics.copy()
    }
  }

  // Time complexity - O(n)
  // Space complexity - O(n)
  def recalculate(): Unit = {
    this.synchronized {
      val newTransactions = transactions
        .filter(transaction => expirationChecker.isNotExpired(transaction.timestamp))

      val newStatistics = newTransactions
        .foldLeft(Statistics.Empty)((acc, transaction) => {
          val sum = acc.sum + transaction.amount
          val count = acc.count + 1
          val max = Math.max(acc.max, transaction.amount)
          val min = Math.min(acc.min, transaction.amount)

          Statistics(sum, sum / count, max, min, count)
        })

      transactions = newTransactions
      statistics = newStatistics
    }
  }
}
