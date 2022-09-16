package caloree.query

import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatimedrivernative._

import cats.syntax.all._

import caloree.model.Types._
import caloree.model.{CustomFood, Food, Log, User}

import java.time.LocalDate

object LogQuery {
  def logByUserAndDate(
      user: UID,
      fid: Option[EFID],
      day: LocalDate,
      offset: Offset,
      page: Page,
      limit: Limit,
      minute: MinuteInterval)(
      implicit l: LogHandler
  ): ConnectionIO[List[Log]] = {
    val MinuteInterval(start, end) = minute
    fid match {
      case Some(Left(fid))  =>
        sql"""
          select food_id, custom_food_id, "day", "minute", description, amount, energy, protein, carbs, fat, fiber
          from   log_with_nutrients_with_offset($offset)
          where  user_id = $user
          and    "day" = $day
          and    amount > 0
          and    "minute" >= $start and "minute" < $end
          and    custom_food_id = $fid
          order by "minute"
          limit  $limit
          offset $page * $limit
        """.query[Log].to
      case Some(Right(fid)) =>
        sql"""
          select food_id, custom_food_id, "day", "minute", description, amount, energy, protein, carbs, fat, fiber
          from   log_with_nutrients_with_offset($offset)
          where  user_id = $user
          and    "day" = $day
          and    amount > 0
          and    "minute" >= $start and "minute" < $end
          and    food_id = $fid
          order by "minute"
          limit  $limit
          offset $page * $limit
        """.query[Log].to
      case None             =>
        sql"""
          select food_id, custom_food_id, "day", "minute", description, amount, energy, protein, carbs, fat, fiber
          from   log_with_nutrients_with_offset($offset)
          where  user_id = $user
          and    "day" = $day
          and    amount > 0
          and    "minute" >= $start and "minute" < $end
          order by "minute"
          limit  $limit
          offset $page * $limit
        """.query[Log].to
    }
  }

  def undoLog(user: UID, day: LocalDate, times: Int)(implicit l: LogHandler): ConnectionIO[Unit] =
    sql"""
      delete from "log"
      where id in (
        select id
        from "log"
        where user_id = $user and "day" = $day
        order by generated_at desc
        limit $times
      )
    """.update.run.as()

  def insertLog(
      fid: EFID,
      amount: Grams,
      day: LocalDate,
      minute: Minute,
      user: UID)(
      implicit l: LogHandler
  ): ConnectionIO[Unit] = {
    val foodId       = fid.toOption
    val customFoodId = fid.swap.toOption
    sql"""
      insert into "log" (food_id, custom_food_id, amount, "day", "minute", user_id)
      values ($foodId, $customFoodId, $amount, $day, $minute, $user)
    """.update.run.as()
  }

  def logDeletion(day: LocalDate, num: Int, user: UID)(implicit l: LogHandler): ConnectionIO[Unit] =
    sql"""
      insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
      select food_id, custom_food_id, -amount, "day", "minute", "user_id"
      from log_aggregated_with_offset(0)
      where "day" = $day and user_id = $user
      order by "minute"
      offset $num limit 1
    """.update.run.as()

  def logModification(newAmount: Grams, day: LocalDate, num: Int, user: UID)(implicit l: LogHandler): ConnectionIO[Unit] =
    sql"""
      insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
      select food_id, custom_food_id, ($newAmount - amount), "day", "minute", "user_id"
      from log_aggregated_with_offset(0)
      where "day" = $day and user_id = $user
      order by "minute"
      offset $num limit 1
    """.update.run.as()

}
