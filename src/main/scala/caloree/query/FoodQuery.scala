package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.Food
import caloree.model.Types._

object FoodQuery {
  def foodById(id: EntityId[Food], amount: Grams)(implicit lh: LogHandler): ConnectionIO[Option[Food]] =
    sql"""
      select id,
             description,
             $amount,
             (energy / 100)  * $amount,
             (protein / 100) * $amount,
             (carbs / 100)   * $amount,
             (fat / 100)     * $amount,
             (fiber / 100)   * $amount
      from foods_with_nutrients_view
      where id = $id
    """.query[Food].option
}
