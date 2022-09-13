package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import caloree.model.Types.{CFID, Description, EntityId, Grams, UID}
import io.circe.{Decoder, Encoder}

case class CustomFood(id: CFID, userId: UID, description: Description, grams: Grams, nutrients: Nutrients)

object CustomFood {
  implicit val foodCirceDecoder: Decoder[CustomFood] = deriveDecoder
  implicit val foodCirceEncoder: Encoder[CustomFood] = deriveEncoder
}
