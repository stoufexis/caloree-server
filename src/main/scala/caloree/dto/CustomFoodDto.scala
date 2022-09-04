package caloree.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.auto._

import cats.effect.kernel.Concurrent

import caloree.model.Nutrients
import caloree.model.Types.Description

case class CustomFoodDto(description: Description, nutrients: Nutrients)

object CustomFoodDto {
  implicit def customFoodDtoEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, CustomFoodDto] = jsonOf[F, CustomFoodDto]
}
