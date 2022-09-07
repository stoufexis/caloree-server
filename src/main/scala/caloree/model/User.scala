package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import caloree.model.Types.{AccessToken, UID, Username}

case class User(id: UID, username: Username)

object User {
  implicit val foodCirceDecoder: Decoder[User] = deriveDecoder
  implicit val foodCirceEncoder: Encoder[User] = deriveEncoder
}
