package caloree.query

import caloree.model.Types._
import caloree.model.{Food, FoodPreview}

import doobie._
import doobie.implicits._

object FoodPreviewQuery {
  private implicit val han: LogHandler = LogHandler.jdkLogHandler

  def foodsPreviewByDescription(description: Description, page: Page, limit: Int): ConnectionIO[List[FoodPreview]] =
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
