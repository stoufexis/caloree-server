package caloree.model

import caloree.model.Types.{Description, EntityId}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

case class CustomFoodPreview(id: EntityId[CustomFood], userId: EntityId[User], description: Description)

object CustomFoodPreview {
  implicit val foodPreviewCirceDecoder: CirceDecoder[CustomFoodPreview] = deriveDecoder
  implicit val foodPreviewCirceEncoder: CirceEncoder[CustomFoodPreview] = deriveEncoder
}
