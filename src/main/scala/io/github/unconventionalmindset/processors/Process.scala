package io.github.unconventionalmindset
package processors

import models.{Measurement, SensorStats}
import processors.Process.joinFiles

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.*
import akka.stream.scaladsl.*
import akka.util.ByteString
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}

import java.io.File
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Success, Try}

class Process(
  fileProcessor: FileProcessor
):
  def process(): Unit =
    val config = ConfigFactory.load()
      .withValue("akka.loglevel", ConfigValueFactory.fromAnyRef("ERROR"))
      .withValue("akka.stdout-loglevel", ConfigValueFactory.fromAnyRef("ERROR"))
    implicit val system: ActorSystem = ActorSystem("HumiditySensorsStatistics", config)
    implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

    val files = fileProcessor.getListOfFiles

    val statistics = files
      .foldLeft(Source.empty[Measurement])(joinFiles)
      .groupBy(Int.MaxValue, measurement => measurement.id)
      .fold(SensorStats.empty)(_ + _)
      .mergeSubstreams
      .runWith(Sink.seq)

    statistics.onComplete {
      case Success(statsSeq: Seq[SensorStats]) =>
        val numProcessedFiles = files.size
        Process.printResultHeader(numProcessedFiles, statsSeq)
        statsSeq
          .sortBy(s => s.avg) // Sort by highest avg humidity
          .foreach(stat => println(stat.printSensorResult))

        system.terminate()
      case Failure(ex) =>
        println(s"Error: ${ex.getMessage}")
        throw ex
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)

object Process:
  private def printResultHeader(
    numProcessedFiles: Int,
    statsSeq: Seq[SensorStats]
  ): Unit =
    val numProcessedMeasurements = statsSeq.map(_.measurementCount).sum
    val numFailedMeasurements = statsSeq.map(_.failedCount).sum
    println(s"""
        |Num of processed files: $numProcessedFiles
        |Num of processed measurements: $numProcessedMeasurements
        |Num of failed measurements: $numFailedMeasurements
        |
        |Sensors with highest avg humidity:
        |sensor-id,min,avg,max""".stripMargin)

  def joinFiles =
    (acc: Source[Measurement, NotUsed], file: File) =>
      val fileSource = FileIO.fromPath(file.toPath)
        .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 1024, allowTruncation = false))
        .map(_.utf8String.replaceAll("\r", "")) // Remove '\r' characters
        .drop(1) // Skip the header line
        .map(line => {
          val Array(sensorId, humidityStr) = line.split(",")
          Measurement(sensorId, Try(humidityStr.toInt).toOption)
        })

      acc.concat(fileSource)