package caloree.db_init

import doobie._
import doobie.implicits._

import cats.syntax.all._

object Views {

  val foodWithNutrientsView: ConnectionIO[Unit] =
    sql"""
      create materialized view foods_with_nutrients_view as
      select f.id,
             f.f_type,
             f.description,
             coalesce(sum(case when n.id = 1008 then fn.amount end), 0::real)  as energy,
             coalesce(sum(case when n.id = 1003 then fn.amount end), 0::real) as protein,
             coalesce(sum(case when n.id = 1005 then fn.amount end), 0::real)   as carbs,
             coalesce(sum(case when n.id = 1004 then fn.amount end), 0::real)     as fat,
             coalesce(sum(case when n.id = 1079 then fn.amount end), 0::real)   as fiber
      from food f 
          join food_nutrient fn on f.id = fn.food_id
          join nutrient n on n.id = fn.nutrient_id
      group by f.id
    """.update.run.as()

  val customFoodWithNutrientsView: ConnectionIO[Unit] =
    sql"""
      create view custom_food_with_nutrients_view as
      select cf.id,
             cf.user_id,
             cf.description,
             coalesce(sum(case when n.id = 1008 then cfn.amount end), 0::real) as energy,
             coalesce(sum(case when n.id = 1003 then cfn.amount end), 0::real) as protein,
             coalesce(sum(case when n.id = 1005 then cfn.amount end), 0::real) as carbs,
             coalesce(sum(case when n.id = 1004 then cfn.amount end), 0::real) as fat,
             coalesce(sum(case when n.id = 1079 then cfn.amount end), 0::real) as fiber
      from custom_food cf
               join custom_food_nutrient cfn on cf.id = cfn.custom_food_id
               join nutrient n on n.id = cfn.nutrient_id
      group by cf.id
    """.update.run.as()

  val logAggregatedView: ConnectionIO[Unit] =
    sql"""
      create view log_aggregated_view as
      select food_id, custom_food_id, "day", "quarter", user_id , sum(amount) as amount from "log"
      group by food_id, custom_food_id, "day", "quarter", user_id
    """.update.run.as()

  val logWithNutrients: ConnectionIO[Unit] =
    sql"""
        create view log_with_nutrients_view as
        select food_id,
               null as custom_food_id,
               "day",
               "quarter",
               f.description,
               lav.user_id,
               amount,
               (fwnv.energy  * (lav.amount / 100))::real as energy,
               (fwnv.protein * (lav.amount / 100))::real as protein,
               (fwnv.carbs   * (lav.amount / 100))::real as carbs,
               (fwnv.fat     * (lav.amount / 100))::real as fat,
               (fwnv.fiber   * (lav.amount / 100))::real as fiber
        from log_aggregated_view lav
            inner join food f on f.id = lav.food_id
            inner join foods_with_nutrients_view fwnv on fwnv.id = f.id
        union
        select null as food_id,
               custom_food_id,
               "day",
               "quarter",
               cf.description,
               lav.user_id,
               amount,
               (cfwnv.energy  * (lav.amount / 100))::real as energy,
               (cfwnv.protein * (lav.amount / 100))::real as protein,
               (cfwnv.carbs   * (lav.amount / 100))::real as carbs,
               (cfwnv.fat     * (lav.amount / 100))::real as fat,
               (cfwnv.fiber   * (lav.amount / 100))::real as fiber
        from log_aggregated_view lav
            inner join custom_food cf on cf.id = lav.custom_food_id
            inner join custom_food_with_nutrients_view cfwnv on cfwnv.id = cf.id
    """.update.run.as()

  val nutrientsOfQuarterView: ConnectionIO[Unit] =
    sql"""
      create view nutrients_of_quarter_view as
      select "day",
             "quarter",
             user_id,
             sum(energy)  as energy,
             sum(protein) as protein,
             sum(carbs)   as carbs,
             sum(fat)     as fat,
             sum(fiber)   as fiber
      from log_with_nutrients_view
      group by "day", "quarter", "user_id"
    """.update.run.as()

  val nutrientsOfDayView: ConnectionIO[Unit] =
    sql"""
      create view nutrients_of_day_view as
      select "day",
             user_id,
             sum(energy)  as energy,
             sum(protein) as protein,
             sum(carbs)   as carbs,
             sum(fat)     as fat,
             sum(fiber)   as fiber
      from log_with_nutrients_view
      group by "day", "user_id"
    """.update.run.as()

  val userWithTargetsView: ConnectionIO[Unit] =
    sql"""
      create view user_with_target_nutrients_view as
      select u.id,
             u.username,
             coalesce(sum(case when n.id = 1008 then utn.amount end), 0::real) as energy,
             coalesce(sum(case when n.id = 1003 then utn.amount end), 0::real) as protein,
             coalesce(sum(case when n.id = 1005 then utn.amount end), 0::real) as carbs,
             coalesce(sum(case when n.id = 1004 then utn.amount end), 0::real) as fat,
             coalesce(sum(case when n.id = 1079 then utn.amount end), 0::real) as fiber
      from "user" u
                 join user_target_nutrients utn on u.id = utn.user_id
                 join nutrient n on n.id = utn.nutrient_id
      group by u.id
    """.update.run.as()

  val all: ConnectionIO[Unit] = for {
    _ <- foodWithNutrientsView
    _ <- customFoodWithNutrientsView
    _ <- logAggregatedView
    _ <- logWithNutrients
    _ <- nutrientsOfQuarterView
    _ <- nutrientsOfDayView
    _ <- userWithTargetsView
  } yield ()
}
