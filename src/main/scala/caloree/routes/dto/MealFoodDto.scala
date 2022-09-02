package caloree.routes.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}

import cats.effect.kernel.Concurrent

import caloree.model.Types.{Description, EntityId, Grams}
import caloree.model.{CustomFood, Food, Meal}

import java.time.LocalDate

case class MealFoodDto(
    foodId: Either[EntityId[CustomFood], EntityId[Food]],
    amount: Grams,
    meal: Either[EntityId[Meal], Description],
    date: LocalDate)

object MealFoodDto {
  implicit def decodeMealFoodDto[F[_]: Concurrent]: EntityDecoder[F, MealFoodDto] =
    jsonOf[F, MealFoodDto]

  implicit val eitherIdsDecoder: Decoder[Either[EntityId[Meal], Description]] =
    Decoder.decodeEither("meal_id", "name")

  implicit val eitherIdsEncoder: Encoder[Either[EntityId[Meal], Description]] =
    Encoder.encodeEither("meal_id", "name")
}
