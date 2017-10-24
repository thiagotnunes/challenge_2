package com.n26.challenge

import java.time.Clock

import com.n26.challenge.handlers.{StatisticsHandler, TransactionsHandler}
import com.n26.challenge.parsers.TransactionParser
import com.n26.challenge.repositories.TransactionsRepository
import com.twitter.util.Duration

object Main {
  def main(args: Array[String]): Unit = {
    val clock = Clock.systemUTC()
    val repository = new TransactionsRepository
    val expirationChecker = new ExpirationChecker(clock, Duration.fromSeconds(60))
    val transactionParser = new TransactionParser(expirationChecker)
    val transactionsHandler = new TransactionsHandler(transactionParser, repository)
    val statisticsCalculator = new StatisticsCalculator(expirationChecker, repository)
    val statisticsHandler = new StatisticsHandler(statisticsCalculator)

    val httpServer = new HttpServer(8080, transactionsHandler, statisticsHandler)

    httpServer.start()
    httpServer.join()
  }
}
