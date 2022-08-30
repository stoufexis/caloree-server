package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{Food, FoodPreview, User}
import caloree.query.Execute
import caloree.routes.Routes._
import caloree.util._

object FoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Execute[F, EntityId[Food], Option[Food]],
      gm: Execute[F, (Description, Page, Int), List[FoodPreview]]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? FoodIdP(id) as _                                       => go.execute(id).asResponse
      case GET -> _ :? DescriptionP(desc) +& PageP(page) +& Limit(limit) as _ =>
        gm.execute((desc, page, limit)).asResponse
    }
  }
}
