package caloree.model

import caloree.model.Types.{Description, EntityId}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import java.time.LocalDate

case class Meal(id: EntityId[Meal], name: Description, user: EntityId[User], day: LocalDate)

object Meal {
  implicit val mealCirceDecoder: CirceDecoder[Meal] = deriveDecoder
  implicit val mealCirceEncoder: CirceEncoder[Meal] = deriveEncoder
}
