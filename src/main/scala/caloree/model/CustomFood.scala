package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{CFID, Description, EntityId, Grams, UID}

case class CustomFood(id: CFID, userId: UID, description: Description, grams: Grams, nutrients: Nutrients)

object CustomFood {
  implicit val foodCirceDecoder: CirceDecoder[CustomFood] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[CustomFood] = deriveEncoder
}
