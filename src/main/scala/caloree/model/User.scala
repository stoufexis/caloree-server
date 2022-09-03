package caloree.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{AccessToken, UID, Username}

case class User(id: UID, username: Username, accessToken: AccessToken)

object User {
  implicit val foodCirceDecoder: CirceDecoder[User] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[User] = deriveEncoder
}
