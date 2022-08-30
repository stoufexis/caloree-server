package caloree.model

import caloree.model.Types.{Description, EntityId}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

case class Food(id: EntityId[Food], description: Description, nutrients: Nutrients)

object Food {
  implicit val foodCirceDecoder: CirceDecoder[Food] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[Food] = deriveEncoder
}
