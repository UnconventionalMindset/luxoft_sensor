package io.github.unconventionalmindset

import akka.stream.*
import io.github.unconventionalmindset.processors.{FileProcessor, Process}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import java.io.{BufferedWriter, ByteArrayOutputStream, File, FileWriter, PrintStream}

class Tests extends AnyFeatureSpec with Matchers:
  Feature("Humidity Sensors Statistics") {
    Scenario("Provided test files & my own") {
      val expectedSensorStats = Seq(
        "1.0@,1,1,1",
        "c1,1,3,6",
        "s1,10,54,98",
        "s2,78,82,88",
        "s3,NaN,NaN,NaN",
        "sensor10,NaN,NaN,NaN",
      )
      val expectedHeader = headerText(
        processedFiles = 3,
        processedMeasurement = 25,
        failedMeasurement = 15
      )
      val capturedOutput = capture(() => Process(processResources).process())

      println(capturedOutput)

      capturedOutput
        .stripPrefix(expectedHeader)
        .trim
        .split("\r\n") should contain theSameElementsAs expectedSensorStats
    }
    Scenario("High file size") {
      val expectedSensorStats = Seq(
        "1.0@,1,1,1",
        "c1,1,3,6",
        "s1,1,1,98",
        "s2,78,82,88",
        "s3,NaN,NaN,NaN",
        "sensor10,NaN,NaN,NaN",
      )
      val expectedHeader = headerText(
        processedFiles = 4,
        processedMeasurement = 3355469,
        failedMeasurement = 15
      )
      val bigFile = createBigFile
      val capturedOutput = capture(() => Process(processResources).process())

      println(capturedOutput)

      if (bigFile.delete())
        println(s"File ${bigFile.getAbsolutePath} deleted successfully.")
      else
        println(s"Failed to delete file ${bigFile.getAbsolutePath}.")

      capturedOutput
        .stripPrefix(expectedHeader)
        .trim
        .split("\r\n") should contain theSameElementsAs expectedSensorStats
    }
  }

  def capture(fn: () => Unit): String =
    val outputStream = ByteArrayOutputStream()
    val printStream = PrintStream(outputStream)
    Console.withOut(printStream) {
      fn()
    }
    outputStream.toString

  def processResources: FileProcessor =
    val resourcesPath = "src/test/resources/"
    val folder = java.io.File(resourcesPath).getAbsolutePath
    FileProcessor(folder)
  def headerText(
    processedFiles: Int,
    processedMeasurement: Int,
    failedMeasurement: Int,
  ): String =
      s"""
        |Num of processed files: $processedFiles
        |Num of processed measurements: $processedMeasurement
        |Num of failed measurements: $failedMeasurement
        |
        |Sensors with highest avg humidity:
        |sensor-id,min,avg,max
        |""".stripMargin

  def createBigFile: File =
    val sixteenMega = 16L
    val fName = s"${sixteenMega}MB_file.csv"
    val firstLine = "sensor-id,humidity\n"
    val lineToRepeat = "s1,1\n"
    val targetFileSize = sixteenMega * 1024L * 1024L
    val path = s"src/test/resources/$fName"
    val bigFile = java.io.File(path)

    if (bigFile.exists() && bigFile.isFile) {
      bigFile.delete()
    }

    val writer = BufferedWriter(FileWriter(bigFile, true))
    var currentSize: Long = 0

    try {
      writer.write(firstLine)
      while (currentSize < targetFileSize) {
        writer.write(lineToRepeat)
        currentSize += lineToRepeat.length
      }
    } finally {
      writer.close()
    }
    bigFile