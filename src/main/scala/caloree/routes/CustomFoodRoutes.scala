package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.model.Types.{Description, EntityId, Page, UID}
import caloree.model.{CustomFood, CustomFoodPreview, User}
import caloree.query.Run
import caloree.util._

import Params._

object CustomFoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Run.Optional[F, (EntityId[CustomFood], UID), CustomFood],
      gm: Run.Many[F, (Description, UID), CustomFoodPreview]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? CustomFoodIdP(id) as u                       => go.run((id, u.id)).asResponse
      case GET -> _ :? DescriptionP(d) +& PageP(p) +& Limit(l) as u => gm.run((d, u.id), p, l).asResponse
    }
  }
}
