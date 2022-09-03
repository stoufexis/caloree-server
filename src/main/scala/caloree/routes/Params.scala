package caloree.routes

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher

import caloree.model.Types.{Description, EntityId, Grams, Page}
import caloree.model.{CustomFood, Food}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Params {
  implicit val localDateQueryParamD: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.localDate(DateTimeFormatter.ISO_LOCAL_DATE)

  object CustomFoodIdP extends QueryParamDecoderMatcher[EntityId[CustomFood]]("custom_food_id")
  object DescriptionP  extends QueryParamDecoderMatcher[Description]("description")
  object PageP         extends QueryParamDecoderMatcher[Page]("page")
  object Limit         extends QueryParamDecoderMatcher[Int]("limit")
  object FoodIdP       extends QueryParamDecoderMatcher[EntityId[Food]]("food_id")
  object DateP         extends QueryParamDecoderMatcher[LocalDate]("date")
  object GramsP        extends QueryParamDecoderMatcher[Grams]("grams")
}
