package io.github.unconventionalmindset

import io.github.unconventionalmindset.models.SensorStats

object Printer:
  def printResultHeader(
    numProcessedFiles: Int,
    statsSeq: Seq[SensorStats]
  ): Unit =
    val numProcessedMeasurements = statsSeq.map(_.measurementCount).sum
    val numFailedMeasurements = statsSeq.map(_.failedCount).sum
    val msg = s"""
                 |Num of processed files: $numProcessedFiles
                 |Num of processed measurements: $numProcessedMeasurements
                 |Num of failed measurements: $numFailedMeasurements
                 |
                 |Sensors with highest avg humidity:
                 |sensor-id,min,avg,max""".stripMargin
    println(msg)