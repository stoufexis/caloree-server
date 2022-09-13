package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import caloree.model.Types.{Description, FID, Grams}

case class Food(id: FID, description: Description, grams: Grams, nutrients: Nutrients)

object Food {
  implicit val foodCirceDecoder: Decoder[Food] = deriveDecoder
  implicit val foodCirceEncoder: Encoder[Food] = deriveEncoder
}
