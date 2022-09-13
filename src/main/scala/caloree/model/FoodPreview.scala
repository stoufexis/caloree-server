package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import caloree.model.Types.{Description, EntityId}

case class FoodPreview(id: EntityId[Food], description: Description)

object FoodPreview {
  implicit val foodPreviewCirceDecoder: Decoder[FoodPreview] = deriveDecoder
  implicit val foodPreviewCirceEncoder: Encoder[FoodPreview] = deriveEncoder
}
