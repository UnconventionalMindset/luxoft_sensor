package io.github.unconventionalmindset
package processors

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.*
import akka.stream.scaladsl.*
import akka.util.ByteString
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import io.github.unconventionalmindset.models.{Measurement, SensorStats}

import java.io.File
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Success}


val config = ConfigFactory.load()
  .withValue("akka.loglevel", ConfigValueFactory.fromAnyRef("ERROR"))
  .withValue("akka.stdout-loglevel", ConfigValueFactory.fromAnyRef("ERROR"))

class Process(
  fileProcessor: FileProcessor
):
  given system: ActorSystem = ActorSystem("HumiditySensorsStatistics", config)

  given dispatcher: ExecutionContextExecutor = system.dispatcher
  def process(): Unit =
    val files = fileProcessor.getListOfFiles

    val stats = files
      .foldLeft(Source.empty[Measurement])(joinFiles)
      .groupBy(Int.MaxValue, measurement => measurement.id)
      .fold(SensorStats.empty)(_ + _)
      .mergeSubstreams
      .runWith(Sink.seq)

    stats.onComplete {
      case Success(statsSeq: Seq[SensorStats]) =>
        val numProcessedFiles = files.size
        Printer.printResultHeader(numProcessedFiles, statsSeq)
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


  def joinFiles = (acc: Source[Measurement, NotUsed], file: File) =>
    acc
      .concat(processFile(file))

  private def processFile(file: File) =
    FileIO
      .fromPath(file.toPath)
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 1024, allowTruncation = false))
      // Remove '\r' characters
      .map(_.utf8String.stripSuffix("\r"))
      .drop(1) // Skip the header line
      .map(Measurement.apply)

