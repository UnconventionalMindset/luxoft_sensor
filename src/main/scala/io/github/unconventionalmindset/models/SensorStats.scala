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
      Seq(this.min, m.humidity).collect { case Some(value) => value }.minOption,
      Seq(this.max, m.humidity).max,
      humiditySum + m.humidity.getOrElse(0),
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