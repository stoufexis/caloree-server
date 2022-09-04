package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import cats.Monad
import cats.syntax.all._

import caloree.model.Types.{Description, FID, Grams}
import caloree.model.{Food, FoodPreview, User}
import caloree.query.Run
import caloree.util._

import Params._

object FoodRoutes {

  def routes[F[_]: Monad](
      implicit
      go: Run.Optional[F, (FID, Grams), Food],
      gm: Run.Many[F, Description, FoodPreview]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    object GramsOpt extends OptionalQueryParamDecoderMatcher[Grams]("amount")

    AuthedRoutes.of {
      case GET -> _ / FID(id) :? GramsOpt(g) as _                   => go.run((id, g getOrElse Grams(100))).asResponse
      case GET -> _ :? DescriptionP(d) +& PageP(p) +& Limit(l) as _ => gm.run(d, p, l).asResponse
    }
  }
}
