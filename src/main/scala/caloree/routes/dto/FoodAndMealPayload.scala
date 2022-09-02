package caloree.routes.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import cats.effect.kernel.Concurrent
import caloree.model.Food
import caloree.model.Types.{Description, EntityId, Grams}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

import java.time.LocalDate

case class FoodAndMealPayload(foodId: EntityId[Food], amount: Grams, mealName: Description, date: LocalDate)

object FoodAndMealPayload {
  implicit def decoderF[F[_]: Concurrent]: EntityDecoder[F, FoodAndMealPayload] = jsonOf[F, FoodAndMealPayload]
}
