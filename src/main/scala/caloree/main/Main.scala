package caloree.main

import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

import org.http4s.HttpApp
import org.http4s.Response.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.Logger

import cats.effect.{IO, IOApp}
import cats.syntax.all._

import caloree.auth.AuthUser
import caloree.configuration.{ApiConfig, Config, DBConfig}
import caloree.logging.DoobieLogger
import caloree.model.Types._
import caloree.model._
import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.query.Repos._
import caloree.query.Run
import caloree.query.Run._
import caloree.routes.Routes

import java.time.LocalDate

import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import pureconfig._
import pureconfig.generic.auto._

object Main extends IOApp.Simple {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory[IO]
  implicit val doobieLogger: LogHandler   = DoobieLogger.apply

  val log: HttpApp[IO] => HttpApp[IO] = Logger.httpApp[IO](logHeaders = false, logBody = true)(_)

  val app: IO[Nothing] =
    ConfigSource.default.load[Config]
      .map { case Config(db, api) =>
        implicit val xa: Transactor[IO]                                  = DBConfig.transactor(db)
        implicit def r1: Optional[IO, (Username, AccessToken), User]     = verifyCredentialsRepo[IO]
        implicit def r2: Unique[IO, (Username, Password), AccessToken]   = getTokenRepo[IO]
        implicit def r5: Many[IO, Description, FoodPreview]              = foodsPreviewByDescriptionRepo[IO]
        implicit def r6: Optional[IO, (EntityId[Food], Grams), Food]     = foodByIdRepo[IO]
        implicit def r7: Many[IO, (EntityId[User], LocalDate), MealFood] = mealFoodByUserAndDateRepo[IO]

        implicit def r4: Optional[IO, (EntityId[CustomFood], EntityId[User]), CustomFood] =
          customFoodByIdRepo[IO]

        implicit def r8: Unique[IO, (EntityId[User], LocalDate, List[MealWithFoods]), Int] =
          mealFoodTransactionRepos[IO]

        implicit def r3: Many[IO, (Description, EntityId[User]), CustomFoodPreview] =
          customFoodsPreviewByDescriptionRepo[IO]

        implicit val auth: AuthMiddleware[IO, User] = AuthUser[IO]

        val app: HttpApp[IO] = log(Routes.routes[IO].orNotFound)

        ApiConfig.server[IO](api)(app)
          .use(_ => IO.never)
      }
      .left
      .map(x => new Exception(x.prettyPrint()))
      .liftTo[IO]
      .flatten

  def run: IO[Unit] = app

}
