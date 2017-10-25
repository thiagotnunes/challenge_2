package com.n26.challenge.repositories

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.models.{Statistics, Transaction}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class StatisticsRepository() {
  private var transactions = new mutable.ArrayBuffer[Transaction]()
  private var statistics = Statistics.Empty

  def insert(transaction: Transaction): Unit = {
    this.synchronized {
      transactions += transaction
    }
  }

  // This is only used by the tests
  def findAll(): Array[Transaction] = {
    this.synchronized {
      val copy = Array.ofDim[Transaction](transactions.length)
      transactions.copyToArray(copy)
      copy
    }
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
  def rebuildStatistics(expirationChecker: ExpirationChecker): Unit = {
    this.synchronized {
      // O(n)
      val newTransactions = transactions
        .filter(transaction => expirationChecker.isNotExpired(transaction.timestamp))

      // O(n)
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
