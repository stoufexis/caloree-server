package caloree.query

import doobie._
import doobie.implicits._
import doobie.implicits.legacy.localdate._

import caloree.model.Types._
import caloree.model.User

import java.time.LocalDate

object DayInstanceQuery {
  def incrementTransaction(date: LocalDate, user: EntityId[User]): ConnectionIO[EntityId[TX]] =
    sql"""
      insert into meal_tx (day, "user")
      values ($date, $user)
    """
      .update
      .withUniqueGeneratedKeys("id")

}
