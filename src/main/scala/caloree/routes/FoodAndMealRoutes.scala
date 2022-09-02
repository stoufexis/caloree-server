package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, DecodeFailure, EntityDecoder}

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

import cats.effect.kernel.Concurrent
import cats.syntax.all._

import caloree.model.Types._
import caloree.model.{CustomFood, Food, User}
import caloree.query.MealFoodQuery.{CustomFoodAndMealParams, FoodAndMealParams}
import caloree.query.Run
import caloree.routes.dto.{CustomFoodAndMealPayload, FoodAndMealPayload}

import java.time.LocalDate

object FoodAndMealRoutes {
  def routes[F[_]: Concurrent](
      implicit
      addF: Run.Unique[F, FoodAndMealParams, Int],
      addCf: Run.Unique[F, CustomFoodAndMealParams, Int]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case req @ POST -> _ / "food" as u =>
        req.req
          .as[FoodAndMealPayload]
          .flatMap { case FoodAndMealPayload(fid, amount, mName, date) =>
            addF.run((fid, amount, mName, u.id, date)).flatMap(Ok(_))
          }
          .recoverWith {
            case e: DecodeFailure => BadRequest(e.getMessage())
          }

      case req @ POST -> _ / "custom" as u =>
        req.req
          .as[CustomFoodAndMealPayload]
          .flatMap { case CustomFoodAndMealPayload(fid, amount, mName, date) =>
            addCf.run((fid, amount, mName, u.id, date)).flatMap(Ok(_))
          }
          .recoverWith {
            case e: DecodeFailure => BadRequest(e.getMessage())
          }
    }
  }
}
