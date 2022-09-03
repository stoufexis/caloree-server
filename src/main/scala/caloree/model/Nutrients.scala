package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{Grams, Kcal}

case class Nutrients(amount: Grams, energy: Kcal, protein: Grams, carbs: Grams, fat: Grams, fiber: Grams)

object Nutrients {
  implicit val foodCirceDecoder: CirceDecoder[Nutrients] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[Nutrients] = deriveEncoder
}
