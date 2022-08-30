package caloree.model

import caloree.model.Types.{Grams, Kcal}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

case class Nutrients(energy: Kcal, protein: Grams, carbs: Grams, fat: Grams, fiber: Grams)

object Nutrients {
  implicit val foodCirceDecoder: CirceDecoder[Nutrients] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[Nutrients] = deriveEncoder

}
