package caloree.routes.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

import cats.effect.kernel.Concurrent

import caloree.model.CustomFood
import caloree.model.Types.{Description, EntityId, Grams}

import java.time.LocalDate

case class CustomFoodAndMealPayload(
    foodId: EntityId[CustomFood],
    amount: Grams,
    mealName: Description,
    date: LocalDate)

object CustomFoodAndMealPayload {
  implicit def decoderCF[F[_]: Concurrent]: EntityDecoder[F, CustomFoodAndMealPayload] =
    jsonOf[F, CustomFoodAndMealPayload]
}
