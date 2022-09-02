package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.model.Types.{Description, EntityId, Grams, Page}
import caloree.model.{Food, FoodPreview, User}
import caloree.query.Run
import caloree.util._

import QParams._

object FoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Run.Optional[F, (EntityId[Food], Grams), Food],
      gm: Run.Many[F, Description, FoodPreview]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? FoodIdP(id) +& GramsP(amount) as _ =>
        go.run((id, amount)).asResponse

      case GET -> _ :? DescriptionP(desc) +& PageP(page) +& Limit(limit) as _ =>
        gm.run(desc, page, limit).asResponse
    }
  }
}
