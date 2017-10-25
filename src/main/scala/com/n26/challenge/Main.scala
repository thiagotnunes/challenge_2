package com.n26.challenge

import com.n26.challenge.config.ProductionConfig
import com.n26.challenge.repositories.StatisticsRepository

object Main {
  def main(args: Array[String]): Unit = {
    val config = new ProductionConfig
    val repository = new StatisticsRepository
    val app = new Application(config, repository)

    app.start()
    app.join()
  }
}
