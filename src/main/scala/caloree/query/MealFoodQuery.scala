package caloree.query

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatimedrivernative._

import cats.syntax.all._

import caloree.model.Types._
import caloree.model.{CustomFood, Food, Log, User}

import java.time.LocalDate

object MealFoodQuery {
  def logByUserAndDate(user: UID, date: LocalDate, page: Page, limit: Int)(
      implicit l: LogHandler): ConnectionIO[List[Log]] =
    sql"""
        select food_id, custom_food_id, "day", "quarter", description, amount, energy, protein, carbs, fat, fiber
        from log_with_nutrients_view
        where user_id = $user
        and   "day"   = $date
        limit $limit
        offset $page * $limit
    """
      .query[Log]
      .to[List]

  def insertLog(fid: FID, amount: Grams, day: LocalDate, quarter: Int, user: UID)(
      implicit l: LogHandler): ConnectionIO[Int] = {
    val foodId       = fid.toOption
    val customFoodId = fid.swap.toOption
    sql"""
      insert into "log" (food_id, custom_food_id, amount, "day", "quarter", user_id) 
      values ($foodId, $customFoodId, $amount, $day, $quarter, $user)
    """.update.run
  }

  def logDeletion(fid: FID, day: LocalDate, quarter: Int, user: UID)(implicit l: LogHandler): ConnectionIO[Int] =
    fid match {
      case Left(fid)  =>
        sql"""
        insert into "log"(food_id, custom_food_id, amount, "day", "quarter", user_id)
        select food_id, custom_food_id, -amount, "day", "quarter", "user_id" from log_aggregated_view
        where user_id = $user and "day" = $day and "quarter" = $quarter and custom_food_id = $fid
      """.update.run
      case Right(fid) =>
        sql"""
        insert into "log"(food_id, custom_food_id, amount, "day", "quarter", user_id)
        select food_id, custom_food_id, -amount, "day", "quarter", "user_id" from log_aggregated_view
        where user_id = $user and "day" = $day and "quarter" = $quarter and food_id = $fid
      """.update.run
    }

  def logModification(fid: FID, newAmount: Grams, day: LocalDate, quarter: Int, user: UID)(
      implicit l: LogHandler): ConnectionIO[Int] =
    fid match {
      case Left(fid)  =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "quarter", user_id)
          select food_id, custom_food_id, ($newAmount - amount), "day", "quarter", "user_id" from log_aggregated_view
          where user_id = $user and "day" = $day and "quarter" = $quarter and custom_food_id = $fid
        """.update.run
      case Right(fid) =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "quarter", user_id)
          select food_id, custom_food_id, ($newAmount - amount), "day", "quarter", "user_id" from log_aggregated_view
          where user_id = $user and "day" = $day and "quarter" = $quarter and food_id = $fid
        """.update.run
    }
}
