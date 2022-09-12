package caloree.query

import doobie._
import doobie.implicits._

import cats.syntax.all._

import caloree.model.Types._
import caloree.model.{Nutrients, User, UserWithNutrients}

object UserQuery {
  def login(username: Username, password: Password)(implicit lh: LogHandler): ConnectionIO[Option[User]] =
    sql"""
      select id, username
      from "user"
      where username        = $username
      and   hashed_password = sha256($password::bytea)            
    """.query[User].option

  def getUserWithNutrients(id: UID)(implicit lh: LogHandler): ConnectionIO[Option[UserWithNutrients]] =
    sql"""
     select id, username, energy, protein, carbs, fat, fiber 
     from user_with_target_nutrients_view 
     where id = $id
    """.query[UserWithNutrients].option

  def upsertTargetNutrients(id: UID, nutrients: Nutrients)(implicit l: LogHandler): ConnectionIO[Unit] = {
    val Nutrients(energy, protein, carbs, fats, fiber) = nutrients
    sql"""
      insert into user_target_nutrients (user_id, nutrient_id, amount) 
      values ($id, 1008, $energy)
      on conflict (user_id, nutrient_id) do update set amount = $energy;

      insert into user_target_nutrients (user_id, nutrient_id, amount) 
      values ($id, 1003, $protein) 
      on conflict (user_id, nutrient_id) do update set amount = $protein;

      insert into user_target_nutrients (user_id, nutrient_id, amount) 
      values ($id, 1005, $carbs)
      on conflict (user_id, nutrient_id) do update set amount = $carbs;

      insert into user_target_nutrients (user_id, nutrient_id, amount) 
      values ($id, 1004, $fats) 
      on conflict (user_id, nutrient_id) do update set amount = $fats;

      insert into user_target_nutrients (user_id, nutrient_id, amount)
      values ($id, 1079, $fiber) 
      on conflict (user_id, nutrient_id) do update set amount = $fiber;
    """.update.run.as()
  }

  def insertDefaultUser(username: Username, password: Password)(implicit l: LogHandler): ConnectionIO[Unit] =
    sql"""
      insert into "user" (username, hashed_password)
      values ($username, sha256($password::bytea)) on conflict (username) do nothing;
    """.update.run.as()

}
