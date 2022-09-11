package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.Types._
import caloree.model.{User, UserWithNutrients}

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

}
