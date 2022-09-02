package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, DecodeFailure}

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

import cats.effect.kernel.Concurrent
import cats.syntax.all._

import caloree.model.Types.Grams
import caloree.model.Types.{EntityId, Grams}
import caloree.model.{CustomFood, Food, Meal, MealFood, User}
import caloree.query.MealFoodQuery.{CustomFoodAndMealParams, FoodAndMealParams}
import caloree.query.Run
import caloree.routes.dto.{CustomFoodAndMealPayload, FoodAndMealPayload, MealFoodDto}

import java.time.LocalDate

object FoodAndMealRoutes {
  def routes[F[_]: Concurrent](
      implicit
      get: Run.Many[F, (EntityId[User], LocalDate), MealFood],
      addF: Run.Unique[F, (EntityId[Food], EntityId[Meal], Grams), Int],
      addCF: Run.Unique[F, (EntityId[CustomFood], EntityId[Meal], Grams, EntityId[User]), Int],
      addFm: Run.Unique[F, FoodAndMealParams, Int],
      addCfm: Run.Unique[F, CustomFoodAndMealParams, Int]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case req @ POST -> _ as u =>
        req.req
          .as[MealFoodDto]
          .flatMap { case MealFoodDto(foodId, amount, meal, date) =>
            val response = (foodId, meal) match {
              case (Right(fid), Left(mid)) => addF.run((fid, mid, amount))
              case (Left(cfid), Left(mid)) => addCF.run((cfid, mid, amount, u.id))
              case (Right(a), Right(b))    => addFm.run((a, amount, b, u.id, date))
              case (Left(a), Right(b))     => addCfm.run((a, amount, b, u.id, date))
            }
            response.flatMap(Ok(_))
          }
    }
  }
}
