package caloree.main

import doobie.util.transactor.Transactor

import org.http4s.HttpRoutes
import org.http4s.Response.http4sKleisliResponseSyntaxOptionT
import org.http4s.ember.server._
import org.http4s.server.AuthMiddleware

import cats.effect.{IO, IOApp}

import caloree.auth.AuthUser
import caloree.logging.Logging
import caloree.model.User
import caloree.query.Repos._
import caloree.routes.Routes

import com.comcast.ip4s._

object Main extends IOApp.Simple {
  def server(routes: HttpRoutes[IO]): IO[Nothing] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(routes.orNotFound)
      .build
      .use(_ => IO.never)

  implicit val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5433/postgres",
    user = "postgres",
    pass = "postgres"
  )

  implicit val auth: AuthMiddleware[IO, User] = AuthUser[IO]

  val routes: HttpRoutes[IO] = Logging(Routes.routes[IO])

  def run: IO[Unit] = server(routes)
}
