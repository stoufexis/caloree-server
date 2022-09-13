package caloree.configuration

import cats.MonadThrow
import cats.syntax.all._

import scala.util.Try

import caloree.model.Types.{Password, Username}

object DefaultUser {
  def get[F[_]: MonadThrow]: F[(Username, Password)] =
    (Try(System.getenv("CALOREE_DEFAULT_USERNAME")).liftTo[F], Try(System.getenv("CALOREE_DEFAULT_PASSWORD")).liftTo[F])
      .mapN((u, p) => (Username(u), Password(p)))

}
