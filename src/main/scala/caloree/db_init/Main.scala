package caloree.db_init

import doobie.implicits._
import cats.effect.{IO, IOApp}
import cats.syntax.all._
import caloree.configuration.{Config, DBConfig}
import doobie.ConnectionIO
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp.Simple {

  val init: IO[ConnectionIO[Unit]] = for {
    insertStaticFoods <- StaticFoods.insertsStaticFoods[IO]
    cio <-
      (for {
        _ <- Tables.all
        _ <- Views.all
        _ <- insertStaticFoods
      } yield ()).pure[IO]
  } yield cio

  def run: IO[Unit] = for {
    config <- ConfigSource
      .default
      .load[Config]
      .map(_.database)
      .left
      .map(e => new Exception(e.prettyPrint()))
      .liftTo[IO]

    ta = DBConfig.transactor[IO](config)
    _ <- init.flatMap(_.transact[IO](ta))
  } yield ()

}
