package caloree.routes

import org.http4s.EntityDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, DecodeFailure, EntityDecoder, EntityEncoder}

import cats.effect.kernel.Concurrent
import cats.syntax.all._

import caloree.model.Types.{EntityId, Grams}
import caloree.model.{Food, Meal, MealFood, User}
import caloree.query.Run
import caloree.routes.dto.MealFoodPayload
import caloree.util._

import java.time.LocalDate

import QParams._

object MealFoodRoutes {

  def routes[F[_]: Concurrent](
      implicit
      get: Run.Many[F, (EntityId[User], LocalDate), MealFood],
      add: Run.Unique[F, (EntityId[Food], EntityId[Meal], Grams), Int]): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? PageP(page) +& DateP(date) +& Limit(limit) as u =>
        get.run((u.id, date), page, limit).asResponse

      case req @ POST -> _ as u =>
        req.req
          .as[MealFoodPayload]
          .flatMap { case MealFoodPayload(fid, mid, amount) =>
            add.run((fid, mid, amount)).flatMap(Ok(_))
          }
          .recoverWith {
            case e: DecodeFailure => BadRequest(e.getMessage())
          }
    }
  }
}
