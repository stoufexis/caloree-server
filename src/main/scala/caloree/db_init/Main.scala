package caloree.db_init

import doobie.ConnectionIO
import doobie.implicits._

import cats.effect.{IO, IOApp}
import cats.syntax.all._

import caloree.configuration.{Config, DBConfig, DefaultUser}

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp.Simple {

  def insertDefaultUser(u: DefaultUser): ConnectionIO[Int] = {
    val username = u.user
    val password = u.password
    sql"""
        insert into "user" (username, hashed_password)
        values ($username, sha256($password::bytea))
    """.update.run
  }

  val init: IO[ConnectionIO[Unit]] = for {
    insertFoods         <- FromFile[IO]("static_foods.sql")
    insertNutrients     <- FromFile[IO]("static_nutrient.sql")
    insertFoodNutrients <- FromFile[IO]("static_food_nutrient.sql")

    defUser <-
      ConfigSource
        .default
        .load[Config]
        .map(_.defaultUser)
        .left
        .map(e => new Exception(e.prettyPrint()))
        .liftTo[IO]

    cio <- (for {
      _ <- Tables.all
      _ <- Views.all
      _ <- insertNutrients
      _ <- insertFoods
      _ <- insertFoodNutrients
      _ <- insertDefaultUser(defUser)
    } yield ()).pure[IO]
  } yield cio

  def run: IO[Unit] = for {
    config <-
      ConfigSource
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
