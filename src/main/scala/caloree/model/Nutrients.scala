package caloree.model

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import cats.effect.kernel.Concurrent

import caloree.model.Types.{Grams, Kcal}

case class Nutrients(energy: Kcal, protein: Grams, carbs: Grams, fat: Grams, fiber: Grams)

object Nutrients {
  implicit val foodCirceDecoder: Decoder[Nutrients]                                  = deriveDecoder
  implicit val foodCirceEncoder: Encoder[Nutrients]                                  = deriveEncoder
  implicit def nutrientsEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Nutrients] = jsonOf
}
