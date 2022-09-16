package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.Types._
import caloree.model.{Food, FoodPreview}

object FoodPreviewQuery {
  def foodsPreviewByDescription(
      description: Description,
      page: Page,
      limit: Limit)(
      implicit lh: LogHandler
  ): ConnectionIO[List[FoodPreview]] = {
    if (description.string.isEmpty) {
      sql"""
        select id, description
        from food
        limit $limit
        offset $page * $limit
      """.query[FoodPreview].to
    } else {
      sql"""
        select id, description
        from food
        where description_tsvector @@ plainto_tsquery('english', $description)
        limit $limit
        offset $page * $limit
      """.query[FoodPreview].to
    }
  }
}
