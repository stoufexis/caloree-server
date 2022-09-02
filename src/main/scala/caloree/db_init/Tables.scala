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
            id                   bigint    primary key,
            f_type               food_type not null,
            description          text      not null,
            description_tsvector tsvector generated always as (to_tsvector('english'::regconfig, description)) stored
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
          id        bigint           primary key,
          name      text             not null,
          unit_name measurement_unit not null
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
            generated_at timestamp default (now() AT TIME ZONE 'utc'::text)
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

  val mealFood: ConnectionIO[Unit] =
    sql"""
      create table meal_food
      (
          id      bigserial primary key,
          food_id bigint  not null references food,
          meal_id bigint  not null references meal,
          amount  integer not null
      )
    """.update.run.as()

  val mealCustomFood: ConnectionIO[Unit] =
    sql"""
      create table meal_custom_food
      (
          id             bigserial primary key,
          custom_food_id bigint not null references custom_food,
          meal_id        bigint not null references meal,
          amount         real   not null
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
    _ <- food
    _ <- customFood
    _ <- nutrient
    _ <- user
    _ <- token
    _ <- foodNutrient
    _ <- customFoodNutrient
    _ <- mealFood
    _ <- mealCustomFood
    _ <- userTargetNutrients
  } yield ()
}
