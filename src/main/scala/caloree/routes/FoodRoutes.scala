package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.Method.GET
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.TracedAuthedRoute.{Route, TracedAuthedRoute}
import caloree.TracedRoute.Trace
import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{Food, FoodPreview, User}
import caloree.query.Execute
import caloree.routes.Routes._
import caloree.util._
import caloree.{ExtractParams, TracedAuthedRoute}

object FoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Execute.Optional[F, EntityId[Food], Food],
      gm: Execute.Many[F, Description, FoodPreview]
  ): TracedAuthedRoute[User, F] = {

    val r1: Route[F, User, EntityId[Food]] = (
      (GET, ExtractParams[F, EntityId[Food]]("food_id")),
      { case (_, id, _) => go.execute(id).asResponse })

    val r2: Route[F, User, (Description, Page, Int)] = (
      (GET, ExtractParams[F, Description, Page, Int]("description", "page", "limit")),
      { case (_, (desc, page, limit), _) => gm.execute(desc, page, limit).asResponse })

    TracedAuthedRoute.route2(r1, r2)
  }
}
