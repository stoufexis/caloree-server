package caloree.main

import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

import org.http4s.HttpApp
import org.http4s.Response.http4sKleisliResponseSyntaxOptionT
import org.http4s.ember.server._
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.Logger

import cats.effect.{IO, IOApp}

import caloree.auth.AuthUser
import caloree.logging.DoobieLogger
import caloree.model.Types._
import caloree.model._
import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.query.{Execute, Repos}
import caloree.routes.Routes

import java.time.LocalDate

import com.comcast.ip4s._
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp.Simple {
  def server(routes: HttpApp[IO]): IO[Nothing] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes)
      .build
      .use(_ => IO.never)

  implicit val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5433/postgres",
    user = "postgres",
    pass = "postgres"
  )

  implicit val logging: LoggerFactory[IO]     = Slf4jFactory[IO]
  implicit val doobieLogger: LogHandler       = DoobieLogger.apply
  implicit val auth: AuthMiddleware[IO, User] = AuthUser[IO]

  implicit def r1: Execute.Optional[IO, (Username, AccessToken), User] =
    Repos.verifyCredentialsRepo[IO]

  implicit def r2: Execute.Unique[IO, (Username, Password), AccessToken] =
    Repos.getTokenRepo[IO]

  implicit def r3: Execute.Many[IO, (Description, EntityId[User]), CustomFoodPreview] =
    Repos.customFoodsPreviewByDescriptionRepo[IO]

  implicit def r4: Execute.Optional[IO, (EntityId[CustomFood], EntityId[User]), CustomFood] =
    Repos.customFoodByIdRepo[IO]

  implicit def r5: Execute.Many[IO, Description, FoodPreview] =
    Repos.foodsPreviewByDescriptionRepo[IO]

  implicit def r6: Execute.Optional[IO, (EntityId[Food], Grams), Food] =
    Repos.foodByIdRepo[IO]

  implicit def r7: Execute.Many[IO, (EntityId[User], LocalDate), MealFood] =
    Repos.mealFoodByUserAndDateRepo[IO]

  implicit def r8: Execute.Unique[IO, (EntityId[User], LocalDate, List[MealWithFoods]), Int] =
    Repos.mealFoodTransactionRepos[IO]

  val app: HttpApp[IO]    = Routes.routes[IO].orNotFound
  val logged: HttpApp[IO] = Logger.httpApp[IO](logHeaders = false, logBody = true)(app)

  def run: IO[Unit] = server(logged)
}
