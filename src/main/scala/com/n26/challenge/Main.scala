package com.n26.challenge

import java.time.Clock

import com.n26.challenge.handlers.{StatisticsHandler, TransactionsHandler}
import com.n26.challenge.parsers.TransactionParser
import com.n26.challenge.calculator.StatisticsCalculator
import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.util.Duration

object Main {
  def main(args: Array[String]): Unit = {
    val clock = Clock.systemUTC()
    val statisticsCalculatorIntervalInMillis = 100
    val expirationChecker = new ExpirationChecker(clock, Duration.fromSeconds(60))
    val repository = new StatisticsRepository(expirationChecker)
    val transactionParser = new TransactionParser(expirationChecker)

    val statisticsCalculator = new StatisticsCalculator(statisticsCalculatorIntervalInMillis, repository)
    val transactionsHandler = new TransactionsHandler(transactionParser, repository)
    val statisticsHandler = new StatisticsHandler(repository)

    val httpServer = new HttpServer(8080, transactionsHandler, statisticsHandler)

    statisticsCalculator.start()
    httpServer.start()
    httpServer.join()
  }
}
