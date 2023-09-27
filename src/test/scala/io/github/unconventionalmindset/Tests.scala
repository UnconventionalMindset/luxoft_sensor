package io.github.unconventionalmindset

import akka.stream.*
import io.github.unconventionalmindset.processors.{FileProcessor, Process}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, PrintStream}

class Tests extends AnyFeatureSpec with Matchers:
  Feature("Humidity Sensors Statistics") {
    Scenario("provided test files + my own") {
      val expected =
        """Num of processed files: 3
          |Num of processed measurements: 25
          |Num of failed measurements: 15
          |
          |Sensors with highest avg humidity:
          |sensor-id,min,avg,max
          |1.0@,1,1,1
          |c1,1,3,6
          |s1,10,54,98
          |s2,78,82,88
          |s3,NaN,NaN,NaN
          |sensor10,NaN,NaN,NaN""".stripMargin
      val path = "src/test/resources"
      val file = java.io.File(path).getAbsolutePath
      val fp = new FileProcessor(file)

      val outputStream = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputStream)
      Console.withOut(printStream) {
        new Process(fp).process()
      }
      val capturedOutput = outputStream.toString

      removeFinalLine(capturedOutput).trim shouldBe expected
    }
  }

  def removeFinalLine(s: String): String =
    s.replaceAll("\\[INFO].*", "")