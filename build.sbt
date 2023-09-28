val AkkaVersion = "2.8.5"
val scalacticVersion = "3.2.17"
val scalaTestVersion = "3.2.17"

lazy val root = project
  .in(file("."))
  .settings(
    mainClass := Some("io.github.unconventionalmindset.main"),
    assemblyJarName := "humiditySensorStatistics.jar",
    name := "HumiditySensorsStatistics",
    version := "1.0.1",
    scalaVersion := "3.3.1",

    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Akka library repository".at("https://repo.akka.io/maven")
    ),

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe" % "config" % "1.4.2",
      "org.scalactic" %% "scalactic" % scalacticVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )