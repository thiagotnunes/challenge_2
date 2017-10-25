package com.n26.challenge

import com.n26.challenge.scheduler.StatisticsCacheBuilder
import com.n26.challenge.config.AppConfig
import com.n26.challenge.handlers.{StatisticsHandler, TransactionsHandler}
import com.n26.challenge.parsers.TransactionRequestParser
import com.n26.challenge.repositories.StatisticsRepository

class Application(config: AppConfig, repository: StatisticsRepository) {
  private val clock = config.clock
  private val transactionTtl = config.transactionTtl
  private val schedulerIntervalMillis = config.schedulerIntervalMillis
  private val port = config.port

  private val expirationChecker = new ExpirationChecker(clock, transactionTtl)
  private val transactionParser = new TransactionRequestParser(expirationChecker)

  private val statisticsCalculatorScheduler = new StatisticsCacheBuilder(schedulerIntervalMillis, repository, expirationChecker)
  private val transactionsHandler = new TransactionsHandler(transactionParser, repository)
  private val statisticsHandler = new StatisticsHandler(repository)

  private val httpServer = new HttpServer(port, transactionsHandler, statisticsHandler)

  def start(): Unit = {
    statisticsCalculatorScheduler.start()
    httpServer.start()
  }

  def join(): Unit = {
    httpServer.join()
  }

  def stop(): Unit = {
    httpServer.stop()
    statisticsCalculatorScheduler.stop()
  }
}
