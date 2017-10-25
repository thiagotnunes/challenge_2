package com.n26.challenge.scheduler

import java.time.{Clock, Instant, ZoneOffset}

import com.n26.challenge.config.AppConfig
import com.n26.challenge.models.{Statistics, Transaction}
import com.n26.challenge.repositories.StatisticsRepository
import com.n26.challenge.{Application, TestConfig}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class StatisticsCacheBuilderSpec extends Specification {

  sequential

  trait Context extends Scope {
    val now: Instant = Instant.now()
    val clock: Clock = Clock.fixed(now, ZoneOffset.UTC)
    val config: AppConfig = new TestConfig(clock)
    val repository: StatisticsRepository = new StatisticsRepository()
    val app: Application = new Application(config, repository)
  }

  "updates statistics by removing transactions older than 60 seconds" in new Context {
    repository.add(Transaction(10, now.minusSeconds(30).toEpochMilli))
    repository.add(Transaction(20, now.minusSeconds(60).toEpochMilli))
    repository.add(Transaction(30, now.minusSeconds(70).toEpochMilli))

    app.start()
    app.stop()

    repository.getStatistics() ==== Statistics(30, 15, 20, 10, 2)
  }

  "does nothing when there are no transactions" in new Context {
    app.start()
    app.stop()

    repository.getStatistics() ==== Statistics.Empty
  }
}
