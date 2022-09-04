package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import caloree.model.Types.{UID, Username}

case class UserWithNutrients(id: UID, username: Username, nutrients: Nutrients)

object UserWithNutrients {
  implicit val userCirceDecoder: Decoder[UserWithNutrients] = deriveDecoder
  implicit val userCirceEncoder: Encoder[UserWithNutrients] = deriveEncoder
}
