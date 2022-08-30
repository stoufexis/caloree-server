package caloree.query

import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{CustomFood, CustomFoodPreview, User}

import doobie._
import doobie.implicits._

object CustomFoodPreviewQuery {

  def customFoodsPreviewByDescription(
      description: Description,
      user: EntityId[User],
      page: Page,
      limit: Int
  ): ConnectionIO[List[CustomFoodPreview]] =
    sql"""
      select id, description
      from custom_food
      where description_tsvector @@ to_tsquery('english', $description)
      and   user_id = $user
      offset $page * $limit
    """
      .query[CustomFoodPreview]
      .stream
      .take(limit)
      .compile
      .toList

}
