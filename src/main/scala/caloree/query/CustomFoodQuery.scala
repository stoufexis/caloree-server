package caloree.query

import doobie._
import doobie.implicits._
import caloree.model.Types._
import caloree.model.{CustomFood, Nutrients, User}

object CustomFoodQuery {
  def customFoodById(id: CFID, user: UID)(implicit lh: LogHandler): ConnectionIO[Option[CustomFood]] =
    sql"""
      select id, user_id, description, energy, protein, carbs, fat, fiber
      from custom_food_with_nutrients_view
      where id      = $id
      and   user_id = $user
    """.query[CustomFood].option

  private def insertToCustomFoodNutrient(customFoodId: CFID, nutrientId: Int, amount: Float): ConnectionIO[Int] =
    sql"""
      insert into custom_food_nutrient (custom_food_id, nutrient_id, amount) 
      values ($customFoodId, $nutrientId, $amount)
    """
      .update.run

  def insertCustomFood(
      id: UID,
      description: Description,
      nutrients: Nutrients
  ): ConnectionIO[Int] = for {
    id <- sql"insert into custom_food (description, user_id) values ($description, $id)"
      .update.withUniqueGeneratedKeys[CFID]("id")

    l1 <- insertToCustomFoodNutrient(id, 1008, nutrients.energy.toFloat)
    l2 <- insertToCustomFoodNutrient(id, 1003, nutrients.protein.toFloat)
    l3 <- insertToCustomFoodNutrient(id, 1005, nutrients.carbs.toFloat)
    l4 <- insertToCustomFoodNutrient(id, 1004, nutrients.fat.toFloat)
    l5 <- insertToCustomFoodNutrient(id, 1079, nutrients.fiber.toFloat)
  } yield l1 + l2 + l3 + l4 + l5

  def deleteCustomFood(id: CFID): ConnectionIO[Int] =
    sql"delete from custom_food where id = $id"
      .update.run
}
