package com.n26.challenge.scheduler

import com.n26.challenge.ExpirationChecker
import com.n26.challenge.repositories.StatisticsRepository
import com.n26.challenge.scheduler.StatisticsCacheBuilder._
import org.quartz._
import org.quartz.impl.StdSchedulerFactory

class StatisticsCacheBuilder(intervalMillis: Long,
                             repository: StatisticsRepository,
                             expirationChecker: ExpirationChecker) {

  private val schedulerFactory = new StdSchedulerFactory
  private val scheduler = schedulerFactory.getScheduler
  private val job = buildJob()
  private val trigger = buildTrigger()

  def start(): Unit = {
    scheduler.getContext.put(RepositoryParam, repository)
    scheduler.getContext.put(ExpirationCheckerParam, expirationChecker)
    scheduler.start()
    scheduler.scheduleJob(job, trigger)
  }

  def stop(): Unit = {
    scheduler.shutdown(true) // waitsForRunningJobsToComplete
  }

  private def buildJob(): JobDetail = {
    JobBuilder
      .newJob(classOf[StatisticsCacheBuilderJob])
      .withIdentity(JobName)
      .build()
  }

  private def buildTrigger(): Trigger = {
    TriggerBuilder
      .newTrigger()
      .withIdentity(TriggerName)
      .startNow()
      .withSchedule(
        SimpleScheduleBuilder
          .simpleSchedule()
          .withIntervalInMilliseconds(intervalMillis)
          .repeatForever()
      )
      .build()
  }
}

object StatisticsCacheBuilder {
  val JobName = "statistics_cache_builder_job"
  val TriggerName = "statistics_cache_builder_trigger"
  val RepositoryParam = "repository"
  val ExpirationCheckerParam = "expiration_checker"
}