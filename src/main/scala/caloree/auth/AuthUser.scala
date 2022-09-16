package caloree.auth

import org.http4s.BasicCredentials
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

import cats.Monad
import cats.data.{Kleisli, OptionT}
import cats.syntax.all._

import caloree.model.Types.{Password, Username}
import caloree.model.User
import caloree.query.Run

object AuthUser {
  def apply[F[_]: Monad](implicit get: Run.Optional[F, (Username, Password), User]): AuthMiddleware[F, User] =
    AuthMiddleware {
      Kleisli { req =>
        OptionT {
          req.headers
            .get[Authorization]
            .map(_.credentials)
            .collect { case BasicCredentials(u, p) => (Username(u), Password(p)) }
            .map(get.run)
            .traverse(identity)
            .map(_.flatten)
        }
      }
    }
}
