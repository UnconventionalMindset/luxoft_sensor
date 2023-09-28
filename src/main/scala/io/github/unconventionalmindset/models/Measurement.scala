package io.github.unconventionalmindset
package models

import scala.annotation.targetName

case class Measurement(id: String, humidity: Option[Int]):
  @targetName("+")
  def +(other: Measurement): Measurement =
    Measurement(this.id, Some(this.humidity.get + other.humidity.get))

object Measurement:
  def apply(line: String): Measurement =
    val splittingRes = line.split(",")
    val id = splittingRes.head
    val humidityStr = splittingRes.tail.head
    val humidity = if humidityStr != "NaN" then Some(humidityStr.toInt) else None
    Measurement(id, humidity)
