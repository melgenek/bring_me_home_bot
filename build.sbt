name := "bring_me_home_bot"

version := "0.1"

scalaVersion := "2.12.8"


val versions = new {
  val bot = "4.0.0-RC2"
  val akka = "2.5.17"
  val akkaHttp = "10.1.5"
  val slick = "3.3.0"
}

libraryDependencies ++= Seq(
  "com.bot4s" %% "telegram-core" % versions.bot,
  "com.bot4s" %% "telegram-akka" % versions.bot,

  "com.typesafe.akka" %% "akka-actor" % versions.akka,
  "com.typesafe.akka" %% "akka-slf4j" % versions.akka,
  "com.typesafe.akka" %% "akka-stream" % versions.akka,
  "com.typesafe.akka" %% "akka-http" % versions.akkaHttp,
  "de.heikoseeberger" %% "akka-http-circe" % "1.22.0",

  "com.typesafe.slick" %% "slick" % versions.slick,
  "com.typesafe.slick" %% "slick-hikaricp" % versions.slick,
  "org.postgresql" % "postgresql" % "42.2.5",
//  "io.micrometer" % "micrometer-core" % "1.1.4",

  "com.iheart" %% "ficus" % "1.4.5",

  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "biz.enef" %% "slogging-slf4j" % "0.6.1",

  "com.oracle.substratevm" % "svm" % "1.0.0-rc16" % "provided",

  "org.scalatest" %% "scalatest" % "3.0.7" % Test
)

assemblyJarName in assembly := "bot.jar"
