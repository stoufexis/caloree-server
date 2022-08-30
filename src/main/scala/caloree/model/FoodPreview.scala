package caloree.model

import caloree.model.Types.{Description, EntityId}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

case class FoodPreview(id: EntityId[Food], description: Description)

object FoodPreview {
  implicit val foodPreviewCirceDecoder: CirceDecoder[FoodPreview] = deriveDecoder
  implicit val foodPreviewCirceEncoder: CirceEncoder[FoodPreview] = deriveEncoder
}
