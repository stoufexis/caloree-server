package caloree.db_init

import doobie._
import doobie.implicits._

import cats.syntax.all._

object Tables {

  val food: ConnectionIO[Unit] = for {
    _ <-
      sql"""
        create table food
        (
            id                   bigint   primary key,
            f_type               varchar  not null,
            description          text     not null,
            description_tsvector tsvector generated always as (to_tsvector('english', description)) stored
        )
      """.update.run
    _ <-
      sql"""
        create index search_food on food using gin (description_tsvector)
        """.update.run
  } yield ()

  val customFood: ConnectionIO[Unit] = for {
    _ <-
      sql"""
        create table custom_food
        (
            id                   bigserial primary key,
            description          text      not null,
            user_id              bigint    not null references "user",
            description_tsvector tsvector  generated
                always as (to_tsvector('english'::regconfig, description)) stored not null
        )
      """.update.run
    _ <-
      sql"""
        create index search_custom_food on custom_food using gin (description_tsvector)
      """.update.run
  } yield ()

  val nutrient: ConnectionIO[Unit] =
    sql"""
      create table nutrient
      (
          id        bigint  primary key,
          "name"    text    not null,
          unit_name varchar not null
      )
    """.update.run.as()

  val user: ConnectionIO[Unit] =
    sql"""
      create table "user"
      (
          id              bigserial primary key,
          username        text      not null unique,
          hashed_password bytea     not null
      )
    """.update.run.as()

  val token: ConnectionIO[Unit] = for {
    _ <-
      sql"""
        create table token
        (
            id           bigserial primary key,
            user_id      bigint    not null references "user",
            token        uuid      default gen_random_uuid() not null,
            generated_at timestamp default (now() at time zone 'utc'::text)
        )
      """.update.run
    _ <-
      sql"""
        create index tokens_by_time_descending on token (generated_at desc)
      """.update.run
  } yield ()

  val foodNutrient: ConnectionIO[Unit] =
    sql"""
      create table food_nutrient
      (
          id          bigint  not null primary key,
          food_id     integer not null references food,
          nutrient_id integer not null references nutrient,
          amount      real    not null,
          constraint unique_food_id_nutrient_id unique (food_id, nutrient_id)
      )
    """.update.run.as()

  val customFoodNutrient: ConnectionIO[Unit] =
    sql"""
      create table custom_food_nutrient
      (
          id             bigserial primary key,
          custom_food_id bigint not null references custom_food,
          nutrient_id    bigint not null references nutrient,
          amount         real   not null,
          constraint unique_custom_food_id_nutrient_id unique (custom_food_id, nutrient_id)
      )
    """.update.run.as()

  val log: ConnectionIO[Unit] =
    sql"""
      create table "log"
      (
          id             bigserial primary key,
          food_id        bigint    references food,
          custom_food_id bigint    references custom_food,
          amount         integer   not null,
          "day"          date      not null,
          "quarter"      smallint  not null,
          user_id        bigint    not null references "user"(id)
          check ( "quarter" >= 0 and "quarter" < 96),
          check ((food_id is not null and custom_food_id is null) 
              or (food_id is null and custom_food_id is not null))
      )
    """.update.run.as()

  val userTargetNutrients: ConnectionIO[Unit] =
    sql"""
      create table user_target_nutrients
      (
          id          bigserial primary key,
          user_id     bigint not null references "user",
          nutrient_id bigint not null references nutrient,
          amount      real   not null
      )
    """.update.run.as()

  val all: ConnectionIO[Unit] = for {
    _ <- user
    _ <- token
    _ <- food
    _ <- customFood
    _ <- nutrient
    _ <- foodNutrient
    _ <- customFoodNutrient
    _ <- log
    _ <- userTargetNutrients
  } yield ()
}
