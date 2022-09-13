package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import caloree.model.Types.{CFID, Description, UID}

case class CustomFoodPreview(id: CFID, userId: UID, description: Description)

object CustomFoodPreview {
  implicit val foodPreviewCirceDecoder: Decoder[CustomFoodPreview] = deriveDecoder
  implicit val foodPreviewCirceEncoder: Encoder[CustomFoodPreview] = deriveEncoder
}
