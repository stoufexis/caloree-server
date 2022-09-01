package caloree.query

import caloree.model.Types._
import caloree.model.User
import doobie._
import doobie.implicits._
import doobie.util.log.{ExecFailure, ProcessingFailure, Success}

object AuthQuery {
  def verifyCredentials(username: Username, accessToken: AccessToken)(implicit lh: LogHandler): ConnectionIO[Option[User]] =
    sql"""
      select s.id, s.username, s.token
      from (select u.id, u.username, token.token
            from token
                     inner join "user" u on u.id = token.user_id
            where username = $username
            order by generated_at desc
            limit 1) as s
      where s.token = $accessToken :: uuid;
    """
      .query[User]
      .option

  def getToken(username: Username, password: Password)(implicit lh: LogHandler): ConnectionIO[AccessToken] =
    sql"""
      insert into token(user_id)
      select id
      from "user"
      where username      = $username
      and hashed_password = sha256($password::bytea)
    """
      .update
      .withUniqueGeneratedKeys("token")

}
