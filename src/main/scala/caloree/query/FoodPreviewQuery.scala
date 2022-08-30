package caloree.query

import caloree.model.Types._
import caloree.model.{Food, FoodPreview}

import doobie._
import doobie.implicits._

object FoodPreviewQuery {
  def foodsPreviewByDescription(description: Description, page: Page, limit: Int): ConnectionIO[List[FoodPreview]] =
    sql"""
      select id, description
      from food
      where description_tsvector @@ to_tsquery('english', $description)
      offset $page * $limit
    """
      .query[FoodPreview]
      .stream
      .take(limit)
      .compile
      .toList
}
