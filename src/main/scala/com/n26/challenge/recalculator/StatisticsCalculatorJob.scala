package com.n26.challenge.recalculator

import com.n26.challenge.repositories.StatisticsRepository
import org.quartz.{Job, JobExecutionContext}

class StatisticsCalculatorJob extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    val repository = context.get("repository").asInstanceOf[StatisticsRepository]
    repository.recalculate()
  }
}