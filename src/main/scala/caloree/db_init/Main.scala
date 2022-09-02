package caloree.db_init

import doobie.implicits._

import cats.effect.{IO, IOApp}
import cats.syntax.all._

import caloree.configuration.{Config, DBConfig}

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp.Simple {

  def run: IO[Unit] = for {
    config <- ConfigSource
      .default
      .load[Config]
      .map(_.database)
      .left
      .map(e => new Exception(e.prettyPrint()))
      .liftTo[IO]

    ta = DBConfig.transactor[IO](config)
    init <- Init[IO]
    _    <- init.transact[IO](ta)
  } yield ()

}
