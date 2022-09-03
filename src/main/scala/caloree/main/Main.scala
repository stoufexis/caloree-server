package caloree.main

import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

import org.http4s.HttpApp
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.Logger

import cats.effect.{IO, IOApp}
import cats.syntax.all._

import caloree.auth.AuthUser
import caloree.configuration.{ApiConfig, Config, DBConfig}
import caloree.logging.DoobieLogger
import caloree.model._
import caloree.query.AllRepos
import caloree.routes.AllRoutes

import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import pureconfig._
import pureconfig.generic.auto._

object Main extends IOApp.Simple {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory[IO]
  implicit val doobieLogger: LogHandler   = DoobieLogger.apply

  val log: HttpApp[IO] => HttpApp[IO] = Logger.httpApp[IO](logHeaders = false, logBody = true)(_)

  val app: IO[Nothing] = ConfigSource.default.load[Config]
    .map { case Config(db, api, _) =>
      implicit val xa: Transactor[IO]             = DBConfig.transactor(db)
      import AllRepos._
      implicit val auth: AuthMiddleware[IO, User] = AuthUser[IO]

      val app: HttpApp[IO] = log(AllRoutes.routes[IO].orNotFound)

      ApiConfig.server[IO](api)(app)
        .use(_ => IO.never)
    }
    .left
    .map(x => new Exception(x.prettyPrint()))
    .liftTo[IO]
    .flatten

  def run: IO[Unit] = app
}