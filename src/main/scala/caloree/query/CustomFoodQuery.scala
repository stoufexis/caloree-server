package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.Types._
import caloree.model.{CustomFood, User}

object CustomFoodQuery {
  def customFoodById(id: EntityId[CustomFood], user: UID)(implicit lh: LogHandler): ConnectionIO[Option[CustomFood]] =
    sql"""
      select id, user_id, description, energy, protein, carbs, fat, fiber
      from custom_food_with_nutrients_view
      where id      = $id
      and   user_id = $user
    """
      .query[CustomFood]
      .option

}
