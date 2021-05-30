ThisBuild / organization := "org.informalgo"
ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "1.0"

lazy val `web-education` = (project in file("."))
  .aggregate(server, client, common.jvm, common.js)

lazy val server = project
  .settings(
    scalaJSProjects := Seq(client),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full),
    libraryDependencies ++= Dependencies.server ++ Seq(guice)
  )
  .enablePlugins(PlayScala)
  .dependsOn(common.jvm)

lazy val client = project
  .settings(
    scalaJSUseMainModuleInitializer := true,
    resolvers += Resolver.sonatypeRepo("releases"),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "org.querki" %%% "jquery-facade" % "2.0"
    ),
    jsDependencies += "org.webjars" % "jquery" % "3.1.0" / "3.1.0/jquery.js" minified "jquery.min.js"
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb, JSDependenciesPlugin)
  .dependsOn(common.js)

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common"))
  .jsConfigure(_.enablePlugins(ScalaJSWeb))

Global / onLoad := (Global / onLoad).value.andThen(state => "project server" :: state)
