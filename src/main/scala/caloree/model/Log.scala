package caloree.model

import doobie.Read
import doobie.implicits.javasql._
import doobie.implicits.javatimedrivernative._

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{Description, EFID, EntityId, Grams, Minute}

import java.time.LocalDate

// TODO: Date needs to have time zone info
case class Log(
    id: EFID,
    day: LocalDate,
    minute: Minute,
    description: Description,
    amount: Grams,
    nutrients: Nutrients
)

object Log {
  private case class LogDto(
      foodId: Option[EntityId[Food]],
      customFoodId: Option[EntityId[CustomFood]],
      day: LocalDate,
      minute: Minute,
      foodDescription: Description,
      amount: Grams,
      nutrients: Nutrients)

  implicit val readMealFood: Read[Log] =
    Read[LogDto].map { case LogDto(foodId, customFoodId, day, minute, foodDescription, amount, nutrients) =>
      // not exhaustive since database ensures one of the cases is matched
      val id = (foodId, customFoodId) match {
        case (Some(id), None) => Right(id)
        case (None, Some(id)) => Left(id)
      }
      Log(id, day, minute, foodDescription, amount, nutrients)
    }

  implicit val foodCirceDecoder: CirceDecoder[Log] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[Log] = deriveEncoder
}
