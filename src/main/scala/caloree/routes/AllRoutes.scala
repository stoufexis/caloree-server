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
      r2: Run.Optional[F, (CFID, UID), CustomFood],
      r3: Run.Optional[F, (FID, Grams), Food],
      r4: Run.Unique[F, (Username, Password), AccessToken],
      r9: Run.Many[F, (Description, UID), CustomFoodPreview],
      r10: Run.Many[F, Description, FoodPreview],
      r11: Run.Many[F, (UID, Offset, LocalDate), Log],
      r12: Run.Unique[F, (ModifyLog, UID), Int],
      r13: Run.Optional[F, UID, UserWithNutrients],
      add: Run.Unique[F, (UID, Description, Nutrients), Int],
      delete: Run.Unique[F, CFID, Int]
  ): HttpRoutes[F] = Router(
    "token"       -> AuthRoutes.routes,
    "user"        -> auth(UserRoutes.routes),
    "log"         -> auth(LogRoutes.routes),
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )
}
