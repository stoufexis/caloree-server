package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{HttpRoutes, QueryParamDecoder}

import cats.effect.kernel.Concurrent

import caloree.model.Types._
import caloree.model._
import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.query.Execute

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Routes {
  implicit val localDateQueryParamD: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.localDate(DateTimeFormatter.BASIC_ISO_DATE)

  object CustomFoodIdP extends QueryParamDecoderMatcher[EntityId[CustomFood]]("custom_food_id")
  object DescriptionP  extends QueryParamDecoderMatcher[Description]("description")
  object PageP         extends QueryParamDecoderMatcher[Page]("page")
  object Limit         extends QueryParamDecoderMatcher[Int]("limit")
  object FoodIdP       extends QueryParamDecoderMatcher[EntityId[Food]]("food_id")
  object DateP         extends QueryParamDecoderMatcher[LocalDate]("date")
  object MealIdP       extends QueryParamDecoderMatcher[EntityId[Meal]]("meal_id")
  object GramsP        extends QueryParamDecoderMatcher[Grams]("grams")

  def routes[F[_]: Concurrent](
      implicit
      auth: AuthMiddleware[F, User],
      r1: Execute.Optional[F, (Username, AccessToken), User],
      r4: Execute.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood],
      r6: Execute.Optional[F, (EntityId[Food], Grams), Food],
      r2: Execute.Unique[F, (Username, Password), AccessToken],
      r8: Execute.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int],
      r3: Execute.Many[F, (Description, EntityId[User]), CustomFoodPreview],
      r5: Execute.Many[F, Description, FoodPreview],
      r7: Execute.Many[F, (EntityId[User], LocalDate), MealFood]
  ): HttpRoutes[F] = Router(
    "auth"        -> AuthRoutes.routes,
    "meal-food"   -> auth(MealFoodRoutes.routes),
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )
}
