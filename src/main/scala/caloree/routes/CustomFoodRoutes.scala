package caloree.routes

import org.http4s.Method.GET
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.TracedAuthedRoute.{Route, TracedAuthedRoute}
import caloree.TracedRoute.Trace
import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{CustomFood, CustomFoodPreview, Food, User}
import caloree.query.Execute
import caloree.routes.Routes._
import caloree.util._
import caloree.{ExtractParams, TracedAuthedRoute}

object CustomFoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Execute.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood],
      gm: Execute.Many[F, (Description, EntityId[User]), CustomFoodPreview]
  ): TracedAuthedRoute[User, F] = {
    val params1 = ExtractParams[F, EntityId[CustomFood]]("custom_food_id")
    val params2 = ExtractParams[F, Description, Page, Int]("description", "page", "limit")

    val r1: Route[F, User, EntityId[CustomFood]] = (
      (GET, params1),
      { case (_, a, User(uid, _, _)) => go.execute((a, uid)).asResponse })

    val r2: Route[F, User, (Description, Page, Int)] = (
      (GET, params2),
      { case (_, (desc, page, limit), User(uid, _, _)) => gm.execute((desc, uid), page, limit).asResponse })

    TracedAuthedRoute.route2(r1, r2)
  }
}
