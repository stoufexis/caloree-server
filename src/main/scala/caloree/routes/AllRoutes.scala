package caloree.routes

import org.http4s.HttpRoutes
import org.http4s.server.{AuthMiddleware, Router}

import cats.effect.kernel.Concurrent

import caloree.model.Types._
import caloree.model._
import caloree.query.MealFoodQuery.{CustomFoodAndMealParams, FoodAndMealParams}
import caloree.query.Run

import java.time.LocalDate

object AllRoutes {

  def routes[F[_]: Concurrent](
      implicit
      auth: AuthMiddleware[F, User],
      r1: Run.Optional[F, (Username, AccessToken), User],
      r2: Run.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood],
      r3: Run.Optional[F, (EntityId[Food], Grams), Food],
      r4: Run.Unique[F, (Username, Password), AccessToken],
      r5: Run.Unique[F, (EntityId[Food], EntityId[Meal], Grams), Int],
      r6: Run.Unique[F, (EntityId[CustomFood], EntityId[Meal], Grams, EntityId[User]), Int],
      r7: Run.Unique[F, FoodAndMealParams, Int],
      r8: Run.Unique[F, CustomFoodAndMealParams, Int],
      r9: Run.Many[F, (Description, EntityId[User]), CustomFoodPreview],
      r10: Run.Many[F, Description, FoodPreview],
      r11: Run.Many[F, (EntityId[User], LocalDate), MealFood]
  ): HttpRoutes[F] = Router(
    "auth"        -> AuthRoutes.routes,
    "meal-food"   -> auth(MealFoodRoutes.routes),
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )
}
