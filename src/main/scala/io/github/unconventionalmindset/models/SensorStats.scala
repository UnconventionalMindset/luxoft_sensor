package io.github.unconventionalmindset
package models

import scala.annotation.targetName

case class SensorStats(
  id: String,
  min: Option[Int],
  max: Option[Int],
  humiditySum: BigInt,
  measurementCount: Int,
  failedCount: Int
):
  @targetName("+")
  def +(m: Measurement): SensorStats =
    SensorStats(
      m.id,
      (this.min.toList ++ m.humidity.toList).reduceOption(_ min _),
      (this.max.toList ++ m.humidity.toList).reduceOption(_ max _),
      m.humidity.map(crtHumidity => humiditySum + crtHumidity).getOrElse(this.humiditySum),
      this.measurementCount + 1,
      this.failedCount + (if (m.humidity.isEmpty) 1 else 0)
    )

  def avg: String =
    if measurementCount == failedCount then
      "NaN"
    else
      (humiditySum / (measurementCount - failedCount)).toString
      
  def printSensorResult =
    s"$id,${min.getOrElse("NaN")},$avg,${max.getOrElse("NaN")}"

object SensorStats:
  def empty: SensorStats = SensorStats("", None, None, BigInt(0), 0, 0)
  
  def apply(measurement: Measurement): SensorStats =
    new SensorStats(
      measurement.id,
      measurement.humidity,
      measurement.humidity,
      BigInt(0),
      1,
      if measurement.humidity.isEmpty then 1 else 0
    )