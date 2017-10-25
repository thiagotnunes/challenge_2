package com.n26.challenge.calculator

import com.n26.challenge.repositories.StatisticsRepository
import org.quartz._
import org.quartz.impl.StdSchedulerFactory

class StatisticsCalculator(intervalMillis: Long, repository: StatisticsRepository) {

  private val schedulerFactory = new StdSchedulerFactory
  private val scheduler = schedulerFactory.getScheduler
  private val job = JobBuilder
    .newJob(classOf[StatisticsCalculatorJob])
    .withIdentity("job1", "group1")
    .build()
  private val trigger = TriggerBuilder
    .newTrigger()
    .withIdentity("trigger1", "group1")
    .startNow()
    .withSchedule(
      SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInMilliseconds(intervalMillis)
        .repeatForever()
    )
    .build()


  def start(): Unit = {
    scheduler.getContext.put("repository", repository)
    scheduler.start()
    scheduler.scheduleJob(job, trigger)
  }

  def stop(): Unit = {
    scheduler.shutdown(true)
  }
}
