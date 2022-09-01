package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.Types._
import caloree.model.{Food, FoodPreview}

object FoodPreviewQuery {
  def foodsPreviewByDescription(
      description: Description,
      page: Page,
      limit: Int)(
      implicit lh: LogHandler): ConnectionIO[List[FoodPreview]] =
    sql"""
      select id, description
      from food
      where description_tsvector @@ to_tsquery('english', $description)
      limit $limit
      offset $page * $limit
    """
      .query[FoodPreview]
      .to[List]
}
