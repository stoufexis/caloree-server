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

  val mealFoodAggregatedView: ConnectionIO[Unit] =
    sql"""
      create view meal_food_aggregated_view as
      select food_id, meal_id, sum(amount) as amount from meal_food
      group by food_id, meal_id
    """.update.run.as()

  val customMealFoodAggregatedView: ConnectionIO[Unit] =
    sql"""
      create view meal_custom_food_aggregated_view as
      select custom_food_id, meal_id, sum(amount) as amount from meal_custom_food
      group by custom_food_id, meal_id
    """.update.run.as()

  val foodInMealView: ConnectionIO[Unit] =
    sql"""
      create view food_in_meal_view as
      select false                                    as custom,
             f.id                                     as food_id,
             m.id                                     as meal_id,
             m.name                                   as meal_name,
             m.user_id,
             m.day,
             f.description,
             mf.amount,
             (fwnv.energy  * (mf.amount / 100))::real as energy,
             (fwnv.protein * (mf.amount / 100))::real as protein,
             (fwnv.carbs   * (mf.amount / 100))::real as carbs,
             (fwnv.fat     * (mf.amount / 100))::real as fat,
             (fwnv.fiber   * (mf.amount / 100))::real as fiber
      from meal m
               join meal_food_aggregated_view mf on m.id = mf.meal_id
               join food f on f.id = mf.food_id
               join foods_with_nutrients_view fwnv on fwnv.id = f.id
      union
      select true                                       as custom,
             cf.id                                      as food_id,
             m.id                                       as meal_id,
             m.name                                     as meal_name,
             m.user_id,
             m.day,
             cf.description,
             mcf.amount,
             (cfwnv.energy  * (mcf.amount / 100))::real as energy,
             (cfwnv.protein * (mcf.amount / 100))::real as protein,
             (cfwnv.carbs   * (mcf.amount / 100))::real as carbs,
             (cfwnv.fat     * (mcf.amount / 100))::real as fat,
             (cfwnv.fiber   * (mcf.amount / 100))::real as fiber
      from meal m
               join meal_custom_food_aggregated_view mcf on m.id = mcf.meal_id
               join custom_food cf on cf.id = mcf.custom_food_id
               join custom_food_with_nutrients_view cfwnv on cfwnv.id = cf.id
    """.update.run.as()

  val nutrientsOfMealView: ConnectionIO[Unit] =
    sql"""
      create view nutrients_of_meal_view as
      select meal_id,
             meal_name,
             "day",
             user_id,
             sum(energy)  as energy,
             sum(protein) as protein,
             sum(carbs)   as carbs,
             sum(fat)     as fat,
             sum(fiber)   as fiber
      from food_in_meal_view
      group by meal_id,user_id, meal_name, "day"
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
      from food_in_meal_view
      group by user_id, "day"
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
    _ <- mealFoodAggregatedView
    _ <- customMealFoodAggregatedView
    _ <- foodInMealView
    _ <- nutrientsOfMealView
    _ <- nutrientsOfDayView
    _ <- userWithTargetsView
  } yield ()
}
