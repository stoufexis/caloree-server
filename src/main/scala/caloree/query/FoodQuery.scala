package caloree.query

import caloree.model.Food
import caloree.model.Types._

import doobie._
import doobie.implicits._

object FoodQuery {
  def foodById(id: EntityId[Food]): ConnectionIO[Option[Food]] =
    sql"""
      select id, description, energy, protein, carbs, fat, fiber
      from foods_with_nutrients_view
      where id = $id
    """
      .query[Food]
      .option
}
