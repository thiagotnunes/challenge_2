package com.n26.challenge.repositories

import java.util
import java.util.Collections

import com.n26.challenge.models.Transaction

class TransactionsRepository {
  private val transactions = Collections.synchronizedList(new util.ArrayList[Transaction]())

  def insert(transaction: Transaction): Unit = {
    transactions.add(transaction)
  }

  def findAll(): util.List[Transaction] = {
    transactions
  }
}
