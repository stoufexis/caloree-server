package caloree.configuration

import doobie.util.log.LogHandler

import cats.MonadThrow
import cats.effect.Sync
import cats.effect.kernel.Async
import cats.syntax.all._

import scala.util.Try

import caloree.model.Types.{Password, Username}
import caloree.query.{Run, UserQuery}

object DefaultUser {
  private val usernameEnv = "DEFAULT_USERNAME"
  private val passwordEnv = "DEFAULT_PASSWORD"

  def createDefaultUser[F[_]](
      implicit
      F: MonadThrow[F],
      update: Run.Update[F, (Username, Password)]
  ): F[Unit] =
    (Try(System.getenv(usernameEnv)).liftTo[F], Try(System.getenv(passwordEnv)).liftTo[F])
      .mapN((u, p) => (Username(u), Password(p)))
      .flatMap(update.run)

}
