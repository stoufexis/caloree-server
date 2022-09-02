package caloree.query

import doobie._
import doobie.implicits._
import doobie.implicits.legacy.localdate._
import caloree.model.Types._
import caloree.model.{CustomFood, Food, Meal, MealFood, User}

import java.time.LocalDate

object MealFoodQuery {
  def mealFoodByUserAndDate(
      user: EntityId[User],
      date: LocalDate,
      page: Page,
      limit: Int)(
      implicit lh: LogHandler): ConnectionIO[List[MealFood]] =
    sql"""
        select custom, food_id, meal_id, meal_name, description, amount, energy, protein, carbs, fat, fiber
        from food_in_meal_view
        where user_id = $user
        and   "day"   = $date
        order by meal_id
        limit $limit
        offset $page * $limit
      """
      .query[MealFood]
      .to[List]

  def insertMeal(
      mealName: Description,
      user: EntityId[User],
      date: LocalDate)(
      implicit lh: LogHandler): ConnectionIO[EntityId[Meal]] =
    sql"""
        insert into meal("name", user_id, "day")
        values ($mealName, $user, $date)
      """
      .update
      .withUniqueGeneratedKeys("id")

  def insertMealFood(
      food: EntityId[Food],
      meal: EntityId[Meal],
      grams: Grams)(
      implicit lh: LogHandler): ConnectionIO[Int] =
    sql"""
        insert into meal_food(food_id, meal_id, amount)
        values ($food , $meal, $grams)
      """
      .update
      .run

  def insertMealCustomFood(
      customFood: EntityId[CustomFood],
      meal: EntityId[Meal],
      grams: Grams,
      user: EntityId[User])(
      implicit lh: LogHandler): ConnectionIO[Int] =
    sql"""
        insert into meal_custom_food(custom_food_id, meal_id, amount)
        select id, $meal, $grams
        from   custom_food
        where  id      = $customFood
        and    user_id = $user
      """
      .update
      .run

  type FoodAndMealParams = (
    EntityId[Food],
      Grams,
      Description,
      EntityId[User],
      LocalDate)

  def insertFoodAndMeal(
      food: EntityId[Food],
      grams: Grams,
      mealName: Description,
      user: EntityId[User],
      date: LocalDate)(
      implicit lh: LogHandler): ConnectionIO[Int] =
    for {
      mid   <- insertMeal(mealName, user, date)
      lines <- insertMealFood(food, mid, grams)
    } yield lines

  type CustomFoodAndMealParams = (
    EntityId[CustomFood],
      Grams,
      Description,
      EntityId[User],
      LocalDate)

  def insertCustomFoodAndMeal(
      food: EntityId[CustomFood],
      grams: Grams,
      mealName: Description,
      user: EntityId[User],
      date: LocalDate)(
      implicit lh: LogHandler): ConnectionIO[Int] =
    for {
      mid   <- insertMeal(mealName, user, date)
      lines <- insertMealCustomFood(food, mid, grams, user)
    } yield lines
}
