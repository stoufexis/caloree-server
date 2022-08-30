package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{Description, EntityId}

case class CustomFood(id: EntityId[Food], userId: EntityId[User], description: Description, nutrients: Nutrients)

object CustomFood {
  implicit val foodCirceDecoder: CirceDecoder[CustomFood] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[CustomFood] = deriveEncoder
}
