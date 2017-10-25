package com.n26.challenge.scheduler

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.repositories.StatisticsRepository
import com.n26.challenge.scheduler.StatisticsCacheBuilder._
import org.quartz.{Job, JobExecutionContext}

class StatisticsCacheBuilderJob extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    val schedulerContext = context.getScheduler.getContext
    val repository = schedulerContext.get(RepositoryParam).asInstanceOf[StatisticsRepository]
    val expirationChecker = schedulerContext.get(ExpirationCheckerParam).asInstanceOf[ExpirationChecker]

    repository.rebuildStatistics(expirationChecker)
  }
}
