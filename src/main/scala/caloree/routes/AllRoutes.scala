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
      addF: Run.Unique[F, FoodAndMealParams, Int],
      addCf: Run.Unique[F, CustomFoodAndMealParams, Int],
      r6: Run.Many[F, (Description, EntityId[User]), CustomFoodPreview],
      r7: Run.Many[F, Description, FoodPreview],
      r8: Run.Many[F, (EntityId[User], LocalDate), MealFood]
  ): HttpRoutes[F] = Router(
    "auth"          -> AuthRoutes.routes,
    "meal-food"     -> auth(MealFoodRoutes.routes),
    "meal-and-food" -> auth(FoodAndMealRoutes.routes),
    "custom-food"   -> auth(CustomFoodRoutes.routes),
    "food"          -> auth(FoodRoutes.routes)
  )
}
