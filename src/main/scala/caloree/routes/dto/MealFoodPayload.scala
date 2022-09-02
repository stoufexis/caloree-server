package caloree.routes.dto

import org.http4s.EntityDecoder

import cats.effect.kernel.Concurrent

import caloree.model.Types.{EntityId, Grams}
import caloree.model.{Food, Meal}
import org.http4s.circe.jsonOf

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

case class MealFoodPayload(foodId: EntityId[Food], mealId: EntityId[Meal], amount: Grams)

object MealFoodPayload {
  implicit def mealFoodPayloadEncoder[F[_]: Concurrent]: EntityDecoder[F, MealFoodPayload] = jsonOf[F, MealFoodPayload]
}
