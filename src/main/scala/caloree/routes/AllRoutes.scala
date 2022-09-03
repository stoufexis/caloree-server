package caloree.routes

import org.http4s.HttpRoutes
import org.http4s.server.{AuthMiddleware, Router}

import cats.effect.kernel.Concurrent

import caloree.dto.ModifyLog
import caloree.model.Types._
import caloree.model._
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
      r9: Run.Many[F, (Description, EntityId[User]), CustomFoodPreview],
      r10: Run.Many[F, Description, FoodPreview],
      r11: Run.Many[F, (EntityId[User], LocalDate), Log],
      r18: Run.Unique[F, (ModifyLog, EntityId[User]), Int]
  ): HttpRoutes[F] = Router(
    "auth"        -> AuthRoutes.routes,
    "meal-food"   -> auth(MealFoodRoutes.routes),
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )
}
