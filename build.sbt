ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val circeVersion   = "0.14.2"
val http4sVersion  = "0.23.15"
val doobieVersion  = "1.0.0-RC1"
val newtypeVersion = "0.4.4"

lazy val root = (project in file("."))
  .settings(
    name := "caloree-server-2",
    libraryDependencies ++= Seq(
      "io.circe"      %% "circe-core"          % circeVersion,
      "io.circe"      %% "circe-generic"       % circeVersion,
      "io.circe"      %% "circe-parser"        % circeVersion,
      "org.http4s"    %% "http4s-core"         % http4sVersion,
      "org.http4s"    %% "http4s-dsl"          % http4sVersion,
      "org.http4s"    %% "http4s-circe"        % http4sVersion,
      "org.http4s"    %% "http4s-ember-server" % http4sVersion,
      "io.estatico"   %% "newtype"             % newtypeVersion,
      "org.tpolecat"  %% "doobie-core"         % doobieVersion,
      "org.tpolecat"  %% "doobie-postgres"     % doobieVersion,
      "org.typelevel" %% "log4cats-slf4j"      % "2.4.0",
      "ch.qos.logback" % "logback-classic"     % "1.2.11" % Runtime
    ),
    scalacOptions ++= Seq("-Ymacro-annotations"),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )
