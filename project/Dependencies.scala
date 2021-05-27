import sbt._

object Dependencies {

  object Versions {
    val cats = "2.1.1"
    val fs2 = "2.4.0"
    val akka = "2.6.14"
    val doobie = "0.9.0"
    val scalaLogging = "3.9.2"
    val logBack = "1.2.3"
    val logOver = "1.7.30"
    val h2 = "1.4.200"
    val openTable = "0.10.0"
    val postgreSql = "42.2.5"
    val scalaTestPlus = "5.1.0"
    val playWebjars = "2.8.0-1"
    val jquery = "3.5.1"
    val bootstrap = "4.6.0"
    val toastr = "2.1.2"
    val fontAwesome = "5.14.0"
  }

  object Libraries {
    val cats = "org.typelevel" %% "cats-core" % Versions.cats
    val fs2Core = "co.fs2" %% "fs2-core" % Versions.fs2
    val fs2IO = "co.fs2" %% "fs2-io" % Versions.fs2
    val scalaTestPlus: ModuleID = "org.scalatestplus.play" %% "scalatestplus-play" % Versions.scalaTestPlus % Test

    val logBackLibs: Seq[ModuleID] = Seq(
      "ch.qos.logback" % "logback-core" % Versions.logBack,
      "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging,
      "org.slf4j" % "log4j-over-slf4j" % Versions.logOver,
      "ch.qos.logback" % "logback-classic" % Versions.logBack % Test
    )
    val dbLibs: Seq[ModuleID] = Seq(
      "com.h2database" % "h2" % Versions.h2 % Test,
      "com.opentable.components" % "otj-pg-embedded" % Versions.openTable % Test,
      "org.postgresql" % "postgresql" % Versions.postgreSql
    )

    val akka: Seq[ModuleID] = Seq(
      "com.typesafe.akka" %% "akka-actor" % Versions.akka,
      "com.typesafe.akka" %% "akka-remote" % Versions.akka
    )
    val doobieLibs: Seq[ModuleID] = Seq(
      "org.tpolecat" %% "doobie-core" % Versions.doobie,
      "org.tpolecat" %% "doobie-h2" % Versions.doobie, // H2 driver 1.4.200 + type mappings.
      "org.tpolecat" %% "doobie-hikari" % Versions.doobie, // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres" % Versions.doobie, // Postgres driver 42.2.12 + type mappings.
      "org.tpolecat" %% "doobie-quill" % Versions.doobie, // Support for Quill 3.5.1
      "org.tpolecat" %% "doobie-specs2" % Versions.doobie % Test, // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % Versions.doobie % Test // ScalaTest support for typechecking statements.
    )
    val webjarsLibs: Seq[ModuleID] = Seq(
      "org.webjars" %% "webjars-play" % Versions.playWebjars,
      "org.webjars" % "bootstrap" % Versions.bootstrap,
      "org.webjars" % "toastr" % Versions.toastr,
      "org.webjars" % "font-awesome" % Versions.fontAwesome
    )
  }

  val server: Seq[ModuleID] = Seq(
    Libraries.cats,
    Libraries.fs2Core,
    Libraries.fs2IO,
    Libraries.scalaTestPlus
  ) ++
    Libraries.logBackLibs ++
    Libraries.dbLibs ++
    Libraries.akka ++
    Libraries.doobieLibs ++
    Libraries.webjarsLibs

}
