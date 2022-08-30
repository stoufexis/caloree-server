package caloree.query

import caloree.model.Types._
import caloree.model.{CustomFood, User}

import doobie._
import doobie.implicits._

object CustomFoodQuery {
  def customFoodById(id: EntityId[CustomFood], user: EntityId[User]): ConnectionIO[Option[CustomFood]] =
    sql"""
      select id, user_id, description, energy, protein, carbs, fat, fiber
      from custom_food_with_nutrients_view
      where id      = $id
      and   user_id = $user
    """
      .query[CustomFood]
      .option

}
