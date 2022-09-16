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
      r1: Run.Optional[F, (Username, Password), User],
      r2: Run.Optional[F, (CFID, UID, Grams), CustomFood],
      r3: Run.Optional[F, (FID, Grams), Food],
      r8: Run.Optional[F, UID, UserWithNutrients],
      r4: Run.Many[F, (Description, UID), CustomFoodPreview],
      r5: Run.Many[F, Description, FoodPreview],
      r6: Run.Many[F, (UID, Option[EFID], Offset, LocalDate, MinuteInterval), Log],
      r7: Run.Update[F, (ModifyLog, UID)],
      r9: Run.Update[F, (UID, Description, Nutrients)],
      r10: Run.Update[F, CFID],
      r11: Run.Update[F, (UID, Nutrients)]
  ): HttpRoutes[F] = Router(
    "user"        -> auth(UserRoutes.routes),
    "log"         -> auth(LogRoutes.routes),
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )
}
