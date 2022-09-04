package caloree.model

import caloree.model.Types.{Description, EntityId, FID, Grams}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

case class Food(id: FID, description: Description, grams: Grams, nutrients: Nutrients)

object Food {
  implicit val foodCirceDecoder: CirceDecoder[Food] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[Food] = deriveEncoder
}
