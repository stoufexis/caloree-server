package caloree.query

import caloree.model.Types._
import caloree.model.{MealFood, User}

import java.time.LocalDate

import doobie._
import doobie.implicits._
import doobie.implicits.legacy.localdate._

object MealFoodQuery {
  def mealFoodByUserAndDate(
      user: EntityId[User],
      date: LocalDate,
      page: Page,
      limit: Int
  ): ConnectionIO[List[MealFood]] =
    sql"""
      select custom, food_id, meal_id, meal_name, description, amount, energy, protein, carbs, fat, fiber
      from food_in_meal_view
      where user_id = $user
      and   "day"   = $date
      order by meal_id
      offset $page * $limit 
    """
      .query[MealFood]
      .stream
      .take(limit)
      .compile
      .toList
}
