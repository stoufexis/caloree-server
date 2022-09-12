package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder}

import cats.syntax.all._
import cats.{Monad, MonadThrow}

import caloree.model.Types.UID
import caloree.model.{Nutrients, User, UserWithNutrients}
import caloree.query.Run
import caloree.util._

object UserRoutes {
  def routes[F[_]: MonadThrow: EntityDecoder[*[_], Nutrients]](
      implicit
      get: Run.Optional[F, UID, UserWithNutrients],
      upsertNutrients: Run.Update[F, (UID, Nutrients)]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ as User(id, _)        => get.run(id).asResponse
      case req @ POST -> _ as User(id, _) =>
        req.decode.foldF(_.asResponse, n => upsertNutrients.run((id, n)) *> Ok())
    }
  }
}
