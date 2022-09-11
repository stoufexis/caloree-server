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

  def undoLog(
      user: UID,
      fid: Option[EFID],
      day: LocalDate,
      minute: MinuteInterval,
      times: Int)(
      implicit l: LogHandler
  ): ConnectionIO[Unit] = {
    val MinuteInterval(start, end) = minute
    fid match {
      case Some(Left(fid))  =>
        sql"""
          delete from log 
          where id in (
            select id
            from "log"
            where user_id = $user and "day" = $day and custom_food_id = $fid and "minute" >= $start and "minute" < $end
            order by generated_at desc
            limit $times
          )
        """.update.run.as()
      case Some(Right(fid)) =>
        sql"""
          delete from log 
          where id in (
            select id
            from "log"
            where user_id = $user and "day" = $day and food_id = $fid and "minute" >= $start and "minute" < $end
            order by generated_at desc
            limit $times
          )
        """.update.run.as()
      case None             =>
        sql"""
          delete from log 
          where id in (
            select id
            from "log"
            where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end
            order by generated_at desc
            limit $times
          )
        """.update.run.as()
    }
  }

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

  def logDeletion(fid: Option[EFID], day: LocalDate, minute: MinuteInterval, user: UID)(
      implicit l: LogHandler): ConnectionIO[Unit] = {
    val MinuteInterval(start, end) = minute
    fid match {
      case Some(Left(fid)) =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
          select food_id, custom_food_id, -amount, "day", "minute", "user_id" 
          from log_aggregated_with_offset(0)
          where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end and custom_food_id = $fid
        """.update.run.as()

      case Some(Right(fid)) =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
          select food_id, custom_food_id, -amount, "day", "minute", "user_id" 
          from log_aggregated_with_offset(0)
          where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end and food_id = $fid
        """.update.run.as()

      case None =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
          select food_id, custom_food_id, -amount, "day", "minute", "user_id" 
          from log_aggregated_with_offset(0)
          where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end
        """.update.run.as()
    }
  }

  def logModification(
      fid: Option[EFID],
      newAmount: Grams,
      day: LocalDate,
      minute: MinuteInterval,
      user: UID)(
      implicit l: LogHandler
  ): ConnectionIO[Unit] = {
    val MinuteInterval(start, end) = minute
    fid match {
      case Some(Left(fid)) =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
          select food_id, custom_food_id, ($newAmount - amount), "day", "minute", "user_id" 
          from log_aggregated_with_offset(0)
          where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end and custom_food_id = $fid
        """.update.run.as()

      case Some(Right(fid)) =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
          select food_id, custom_food_id, ($newAmount - amount), "day", "minute", "user_id" 
          from log_aggregated_with_offset(0)
          where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end and food_id = $fid
        """.update.run.as()

      case None =>
        sql"""
          insert into "log"(food_id, custom_food_id, amount, "day", "minute", user_id)
          select food_id, custom_food_id, ($newAmount - amount), "day", "minute", "user_id" 
          from log_aggregated_with_offset(0)
          where user_id = $user and "day" = $day and "minute" >= $start and "minute" < $end
        """.update.run.as()
    }
  }
}
