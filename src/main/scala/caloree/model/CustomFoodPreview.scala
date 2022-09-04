package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{CFID, Description, UID}

case class CustomFoodPreview(id: CFID, userId: UID, description: Description)

object CustomFoodPreview {
  implicit val foodPreviewCirceDecoder: CirceDecoder[CustomFoodPreview] = deriveDecoder
  implicit val foodPreviewCirceEncoder: CirceEncoder[CustomFoodPreview] = deriveEncoder
}
