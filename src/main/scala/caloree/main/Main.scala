package caloree.main

import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

import org.http4s.HttpApp
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.Logger

import cats.effect.{IO, IOApp}
import cats.syntax.all._

import caloree.auth.AuthUser
import caloree.configuration.{DBConfig, DefaultUser, MakeServer}
import caloree.logging.DoobieLogger
import caloree.model.Types.{Password, Username}
import caloree.model._
import caloree.query.{AllRepos, Run, UserQuery}
import caloree.routes.AllRoutes

import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp.Simple {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory[IO]
  implicit val doobieLogger: LogHandler   = DoobieLogger.apply

  val log: HttpApp[IO] => HttpApp[IO] = Logger.httpApp[IO](logHeaders = false, logBody = true)(_)

  val app: IO[Unit] =
    (DBConfig.get[IO], DefaultUser.get[IO])
      .flatMapN { case (db, (u, p)) =>
        implicit val xa: Transactor[IO] = DBConfig.transactor(db)

        import AllRepos._

        implicit val auth: AuthMiddleware[IO, User] = AuthUser[IO]

        val app: HttpApp[IO] = log(AllRoutes.routes[IO].orNotFound)

        for {
          _ <- DBConfig.fly4sRes[IO](db).use(_ => IO.unit)
          _ <- implicitly[Run.Update[IO, (Username, Password)]].run((u, p))
          _ <- MakeServer.apply[IO](app).use(_ => IO.never)
        } yield ()
      }

  def run: IO[Unit] = app
}
