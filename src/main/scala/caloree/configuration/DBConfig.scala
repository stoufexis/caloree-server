package caloree.configuration

import doobie.util.transactor.Transactor

import cats.effect.Async

case class DBConfig(driver: String, url: String, user: String, pass: String)

object DBConfig {
  def transactor[F[_]: Async](config: DBConfig): Transactor[F] = {
    val DBConfig(driver, url, user, pass) = config
    Transactor.fromDriverManager[F](driver, url, user, pass)
  }
}
