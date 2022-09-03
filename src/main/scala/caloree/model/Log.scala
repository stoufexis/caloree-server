package caloree.model

import doobie.Read
import doobie.implicits.javasql._
import doobie.implicits.javatimedrivernative._

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{Description, EntityId, FID}

import java.time.LocalDate

// TODO: Date needs to have time zone info
case class Log(
    id: FID,
    day: LocalDate,
    quarter: Int,
    foodDescription: Description,
    nutrients: Nutrients)

object Log {

  private case class LogIn(
      foodId: Option[EntityId[Food]],
      customFoodId: Option[EntityId[CustomFood]],
      day: LocalDate,
      quarter: Int,
      foodDescription: Description,
      nutrients: Nutrients)

  implicit val readMealFood: Read[Log] =
    Read[LogIn].map { case LogIn(foodId, customFoodId, day, quarter, foodDescription, nutrients) =>
      // not exhaustive since database ensures one of the cases is matched
      val id = (foodId, customFoodId) match {
        case (Some(id), None) => Right(id)
        case (None, Some(id)) => Left(id)
      }
      Log(id, day, quarter, foodDescription, nutrients)
    }

  implicit val foodCirceDecoder: CirceDecoder[Log] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[Log] = deriveEncoder
}
