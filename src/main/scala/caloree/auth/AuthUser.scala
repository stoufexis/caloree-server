package caloree.auth

import org.http4s.server.AuthMiddleware

import cats.Monad
import cats.data.{Kleisli, OptionT}
import cats.syntax.all._

import caloree.model.Types.{AccessToken, Username}
import caloree.model.User
import caloree.query.Execute
import caloree.util.extractHeaders

object AuthUser {
  def apply[F[_]: Monad](implicit get: Execute.Optional[F, (Username, AccessToken), User]): AuthMiddleware[F, User] =
    AuthMiddleware {
      Kleisli { req =>
        OptionT {
          extractHeaders(req.headers, ("USER-ID", "AUTH-TOKEN"), Username(_), AccessToken(_))
            .map(get.execute)
            .traverse(identity)
            .map(_.flatten)
        }
      }
    }
}
