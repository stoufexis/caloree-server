package caloree.query

import doobie._
import doobie.implicits._
import doobie.implicits.legacy.localdate._

import cats.syntax.all._

import caloree.model.Types._
import caloree.model.{CustomFood, Food, Meal, User}

import java.time.LocalDate

object DayInstanceQuery {
  private implicit val han: LogHandler = LogHandler.jdkLogHandler

  def incrementTransaction(date: LocalDate, user: EntityId[User]): ConnectionIO[EntityId[TX]] =
    sql"""
      insert into meal_tx (day, "user")
      values ($date, $user)
    """
      .update
      .withUniqueGeneratedKeys("id")

  def insertMeal(
      description: Description,
      user: EntityId[User],
      date: LocalDate,
      tx: EntityId[TX]
  ): ConnectionIO[EntityId[Meal]] =
    sql"""
      insert into meal(name, user_id, day, tx)
      values ($description, $user, $date, $tx)
    """
      .update
      .withUniqueGeneratedKeys("id")

  def insertFoodToMeal(food: EntityId[Food], meal: EntityId[Meal], grams: Grams): ConnectionIO[Int] =
    sql"""
      insert into meal_food(food_id, meal_id, amount)
      values ($food , $meal, $grams)
    """
      .update
      .run

  def insertCustomFoodToMeal(
      meal: EntityId[Meal],
      grams: Grams,
      customFood: EntityId[CustomFood],
      user: EntityId[User]): ConnectionIO[Int] =
    sql"""
      insert into meal_custom_food(custom_food_id, meal_id, amount)
      select id, $meal, $grams
      from   custom_food
      where  id      = $customFood
      and    user_id = $user
    """
      .update
      .run

  type FoodWithAmount = (Either[EntityId[CustomFood], EntityId[Food]], Grams)
  type MealWithFoods  = (Description, List[FoodWithAmount])

  def mealFoodsTransaction(user: EntityId[User], date: LocalDate, meals: List[MealWithFoods]): ConnectionIO[Int] =
    for {
      txId  <- incrementTransaction(date, user)
      lines <- meals.traverse { case (desc, foods) =>
        for {
          mid   <- insertMeal(desc, user, date, txId)
          lines <- foods.traverse {
            case (Right(fid), grams) => insertFoodToMeal(fid, mid, grams)
            case (Left(cfid), grams) => insertCustomFoodToMeal(mid, grams, cfid, user)
          }
        } yield lines
      }
    } yield lines.flatten.sum

}
