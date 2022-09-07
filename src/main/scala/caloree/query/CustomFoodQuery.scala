package caloree.query

import doobie._
import doobie.implicits._

import cats.syntax.all._

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

  private def insertToCustomFoodNutrient(customFoodId: CFID, nutrientId: Int, amount: Float): ConnectionIO[Unit] =
    sql"""
      insert into custom_food_nutrient (custom_food_id, nutrient_id, amount) 
      values ($customFoodId, $nutrientId, $amount)
    """.update.run.as()

  def insertCustomFood(id: UID, description: Description, nutrients: Nutrients): ConnectionIO[Unit] = for {
    id <- sql"insert into custom_food (description, user_id) values ($description, $id)"
      .update.withUniqueGeneratedKeys[CFID]("id")

    _ <- insertToCustomFoodNutrient(id, 1008, nutrients.energy.toFloat)
    _ <- insertToCustomFoodNutrient(id, 1003, nutrients.protein.toFloat)
    _ <- insertToCustomFoodNutrient(id, 1005, nutrients.carbs.toFloat)
    _ <- insertToCustomFoodNutrient(id, 1004, nutrients.fat.toFloat)
    _ <- insertToCustomFoodNutrient(id, 1079, nutrients.fiber.toFloat)
  } yield ()

  def deleteCustomFood(id: CFID): ConnectionIO[Unit] =
    sql"delete from custom_food where id = $id"
      .update.run.as()
}
