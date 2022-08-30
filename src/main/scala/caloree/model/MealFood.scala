package caloree.model

import doobie.util.{Put, Read}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}

import caloree.model.Types.{Description, EntityId, Grams}
import caloree.util._

// TODO: Date needs to have time zone info
case class MealFood(
    id: Either[EntityId[CustomFood], EntityId[Food]],
    mealId: EntityId[Meal],
    mealName: Description,
    foodDescription: Description,
    amount: Grams,
    nutrients: Nutrients)

object MealFood {

  private case class MealFoodIn(
      custom: Boolean,
      id: Long,
      mealId: EntityId[Meal],
      mName: Description,
      fDesc: Description,
      amount: Grams,
      nutrients: Nutrients)

  implicit val readMealFood: Read[MealFood] =
    Read[MealFoodIn].map { case MealFoodIn(custom, id, mealId, mName, fDesc, amount, nutrients) =>
      val eid = !custom cond (EntityId[Food](id), EntityId[CustomFood](id))
      MealFood(eid, mealId, mName, fDesc, amount, nutrients)
    }

  implicit val eitherIdsDecoder: CirceDecoder[Either[EntityId[CustomFood], EntityId[Food]]] =
    CirceDecoder.decodeEither("custom_food_id", "food_id")

  implicit val eitherIdsEncoder: CirceEncoder[Either[EntityId[CustomFood], EntityId[Food]]] =
    CirceEncoder.encodeEither("custom_food_id", "food_id")

  implicit val foodCirceDecoder: CirceDecoder[MealFood] = deriveDecoder
  implicit val foodCirceEncoder: CirceEncoder[MealFood] = deriveEncoder
}
