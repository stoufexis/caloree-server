package caloree.routes

import cats.Monad
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl
import cats.syntax.all._

import caloree.model.Types.UID
import caloree.model.{User, UserWithNutrients}
import caloree.query.Run
import caloree.util._

object UserRoutes {
  def routes[F[_]: Monad](implicit get: Run.Optional[F, UID, UserWithNutrients]): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ as User(id, _) => get.run(id).asResponse
    }
  }
}
