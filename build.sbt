ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val circeVersion      = "0.14.2"
val http4sVersion     = "0.23.15"
val doobieVersion     = "1.0.0-RC1"
val newtypeVersion    = "0.4.4"
val log4catsVersion   = "2.4.0"
val logbackVersion    = "1.2.11"
val simulacrumVersion = "1.0.1"
val pureconfigVersion = "0.17.1"
val fly4sVersion      = "0.0.13"

lazy val root = (project in file("."))
  .settings(
    name := "caloree-server-2",
    libraryDependencies ++= Seq(
      "io.circe"              %% "circe-core"           % circeVersion,
      "io.circe"              %% "circe-generic"        % circeVersion,
      "io.circe"              %% "circe-parser"         % circeVersion,
      "io.circe"              %% "circe-generic-extras" % circeVersion,
      "org.http4s"            %% "http4s-core"          % http4sVersion,
      "org.http4s"            %% "http4s-dsl"           % http4sVersion,
      "org.http4s"            %% "http4s-circe"         % http4sVersion,
      "org.http4s"            %% "http4s-ember-server"  % http4sVersion,
      "io.estatico"           %% "newtype"              % newtypeVersion,
      "org.tpolecat"          %% "doobie-core"          % doobieVersion,
      "org.tpolecat"          %% "doobie-postgres"      % doobieVersion,
      "org.typelevel"         %% "log4cats-slf4j"       % log4catsVersion,
      "ch.qos.logback"         % "logback-classic"      % logbackVersion % Runtime,
      "org.typelevel"         %% "simulacrum"           % simulacrumVersion,
      "com.github.pureconfig" %% "pureconfig"           % pureconfigVersion,
      "com.github.geirolz"    %% "fly4s-core"           % fly4sVersion
    ),
    scalacOptions ++= Seq("-Ymacro-annotations"),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )
