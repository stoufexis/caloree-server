package caloree.routes

import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{HttpRoutes, QueryParamDecoder}

import cats.Monad

import caloree.auth.AuthUser
import caloree.model.Types.{AccessToken, Description, EntityId, Grams, Page, Password, Username}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, Meal, MealFood, User}
import caloree.query.Execute

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Routes {
  private implicit val localDateQueryParamD: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.localDate(DateTimeFormatter.BASIC_ISO_DATE)

  object CustomFoodIdP extends QueryParamDecoderMatcher[EntityId[CustomFood]]("custom_food_id")
  object DescriptionP  extends QueryParamDecoderMatcher[Description]("description")
  object PageP         extends QueryParamDecoderMatcher[Page]("page")
  object Limit         extends QueryParamDecoderMatcher[Int]("limit")
  object FoodIdP       extends QueryParamDecoderMatcher[EntityId[Food]]("food_id")
  object DateP         extends QueryParamDecoderMatcher[LocalDate]("date")
  object MealIdP       extends QueryParamDecoderMatcher[EntityId[Meal]]("meal_id")
  object GramsP        extends QueryParamDecoderMatcher[Grams]("grams")

  def routes[F[_]: Monad](
      implicit
      auth: AuthMiddleware[F, User],
      r1: Execute[F, (Username, AccessToken), Option[User]],
      r2: Execute[F, (Username, Password), AccessToken],
      r3: Execute[F, (Description, EntityId[User], Page, Int), List[CustomFoodPreview]],
      r4: Execute[F, (EntityId[CustomFood], EntityId[User]), Option[CustomFood]],
      r5: Execute[F, (Description, Page, Int), List[FoodPreview]],
      r6: Execute[F, EntityId[Food], Option[Food]],
      r7: Execute[F, (EntityId[User], LocalDate, Page, Int), List[MealFood]]
  ): HttpRoutes[F] = Router(
    "auth"        -> AuthRoutes.routes,
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )

}
