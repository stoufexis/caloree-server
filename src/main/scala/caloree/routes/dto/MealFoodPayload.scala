package caloree.routes.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

import cats.effect.Concurrent

import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.model.MealFood._

import java.time.LocalDate

case class MealFoodPayload(date: LocalDate, mfs: List[MealWithFoods])

object MealFoodPayload {
  implicit def mealFoodPayloadDecoder[F[_]: Concurrent]: EntityDecoder[F, MealFoodPayload] = jsonOf[F, MealFoodPayload]
  implicit val mealFoodPayloadCirceEncoder: Encoder[MealFoodPayload]                       = deriveEncoder
  implicit val mealFoodPayloadCirceDecoder: Decoder[MealFoodPayload]                       = deriveDecoder
}
