package caloree.configuration

import doobie.util.transactor.Transactor

import cats.effect.{Async, Resource}
import cats.syntax.all._

import fly4s.core.Fly4s
import fly4s.core.data.{Fly4sConfig, Location, MigrateResult, ValidatedMigrateResult}
import fly4s.implicits._
import fly4s.syntax._

case class DBConfig(
    driver: String,
    url: String,
    user: String,
    pass: String,
    migrationsTable: String,
    migrationsLocation: String,
    defaultUsername: String,
    defaultPassword: String
)

object DBConfig {
  def transactor[F[_]: Async](config: DBConfig): Transactor[F] = Transactor.fromDriverManager[F](
    driver = config.driver,
    url = config.url,
    user = config.user,
    pass = config.pass
  )

  def fly4sRes[F[_]: Async](config: DBConfig): Resource[F, MigrateResult] = Fly4s.make[F](
    url = config.url,
    user = config.user.some,
    password = config.pass.toCharArray.some,
    config = Fly4sConfig(
      table = config.migrationsTable,
      locations = Location.of(config.migrationsLocation),
      sqlMigrationPrefix = "V",
    )
  ).evalMap(_.migrate)
}
