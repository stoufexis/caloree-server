package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.TracedAuthedRoute
import caloree.TracedAuthedRoute.TracedAuthedRoute
import caloree.TracedRoute.Trace
import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{Food, FoodPreview, User}
import caloree.query.Execute
import caloree.routes.Routes._
import caloree.util._

object FoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Execute.Optional[F, EntityId[Food], Food],
      gm: Execute.Many[F, Description, FoodPreview]
  ): TracedAuthedRoute[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    val trace = Trace.PathNode(
      Nil,
      List(
        Trace.PathLeaf(GET, List("food_id"), Nil),
        Trace.PathLeaf(GET, List("description", "page", "limit"), Nil)))

    TracedAuthedRoute.of(trace) {
      case GET -> _ :? FoodIdP(id) as _                                       => go.execute(id).asResponse
      case GET -> _ :? DescriptionP(desc) +& PageP(page) +& Limit(limit) as _ =>
        gm.execute(desc, page, limit).asResponse
    }
  }
}
