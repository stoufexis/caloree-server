package caloree.query

import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{CustomFood, CustomFoodPreview, User}

import doobie._
import doobie.implicits._

object CustomFoodPreviewQuery {
  private implicit val han: LogHandler = LogHandler.jdkLogHandler

  def customFoodsPreviewByDescription(
      description: Description,
      user: EntityId[User],
      page: Page,
      limit: Int
  ): ConnectionIO[List[CustomFoodPreview]] =
    sql"""
      select id, description
      from   custom_food
      where  description_tsvector @@ to_tsquery('english', $description)
      and    user_id = $user
      limit  $limit
      offset $page * $limit
    """
      .query[CustomFoodPreview]
      .to[List]

}
