package com.n26.challenge.calculator

import java.time.{Clock, Instant, ZoneOffset}

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.models.{Statistics, Transaction}
import com.n26.challenge.repositories.StatisticsRepository
import com.twitter.util.Duration
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class StatisticsCalculatorSpec extends Specification {

  sequential

  trait Context extends Scope {
    val now: Instant = Instant.now()
    val schedulerIntervalInMillis: Long = 10
    private val expirationChecker = new ExpirationChecker(Clock.fixed(now, ZoneOffset.UTC), Duration.fromSeconds(60))
    val repository: StatisticsRepository = new StatisticsRepository(expirationChecker)
    val calculator: StatisticsCalculator = new StatisticsCalculator(schedulerIntervalInMillis, repository)
  }

  "updates statistics by removing transactions older than 60 seconds" in new Context {
    repository.add(Transaction(10, now.minusSeconds(30).toEpochMilli))
    repository.add(Transaction(20, now.minusSeconds(60).toEpochMilli))
    repository.add(Transaction(30, now.minusSeconds(70).toEpochMilli))

    calculator.start()
    calculator.stop() // Blocks until all jobs finished

    repository.getStatistics() ==== Statistics(30, 15, 20, 10, 2)
  }

  "does nothing when there are no transactions" in new Context {
    calculator.start()
    calculator.stop()

    repository.getStatistics() ==== Statistics.Empty
  }
}
