package caloree.model

import caloree.model.Types.{AccessToken, EntityId, Username}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

case class User(id: EntityId[User], username: Username, accessToken: AccessToken)

object User {
  implicit val foodCirceDecoder: CirceDecoder[User] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[User] = deriveEncoder
}
