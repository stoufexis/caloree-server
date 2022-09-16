package caloree.query

import doobie.LogHandler
import doobie.util.transactor.Transactor

import cats.effect.MonadCancelThrow

import caloree.dto.ModifyLog
import caloree.model.Types._
import caloree.model._

import java.time.LocalDate

object AllRepos {
  implicit def verifyCredentialsRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (Username, Password), User] =
    Run.option((UserQuery.login _).tupled)

  implicit def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (Description, UID), CustomFoodPreview] =
    Run.many { case ((d, u), p, l) => CustomFoodPreviewQuery.customFoodsPreviewByDescription(d, u, p, l) }

  implicit def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (CFID, UID, Grams), CustomFood] =
    Run.option((CustomFoodQuery.customFoodById _).tupled)

  implicit def foodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, Description, FoodPreview] =
    Run.many { case (desc, page, limit) => FoodPreviewQuery.foodsPreviewByDescription(desc, page, limit) }

  implicit def foodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (FID, Grams), Food] =
    Run.option((FoodQuery.foodById _).tupled)

  implicit def mealFoodByUserAndDateRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (UID, Option[EFID], Offset, LocalDate, MinuteInterval), Log] =
    Run.many { case ((u, i, o, d, m), page, limit) => LogQuery.logByUserAndDate(u, i, d, o, page, limit, m) }

  implicit def insertMealFoodRepos[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Update[F, (ModifyLog, UID)] =
    Run.update {
      case (ModifyLog.Add(fid, amount, day, minute), u) => LogQuery.insertLog(fid, amount, day, minute, u)
      case (ModifyLog.Remove(day, num), u)              => LogQuery.logDeletion(day, num, u)
      case (ModifyLog.Modify(newAmount, day, num), u)   => LogQuery.logModification(newAmount, day, num, u)
      case (ModifyLog.Undo(day, num), u)                => LogQuery.undoLog(u, day, num)
    }

  implicit def getUserWithNutrients[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, UID, UserWithNutrients] =
    Run.option(UserQuery.getUserWithNutrients)

  implicit def insertCustomFood[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Update[F, (UID, Description, Nutrients)] =
    Run.update((CustomFoodQuery.insertCustomFood _).tupled)

  implicit def deleteCustomFood[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Update[F, CFID] =
    Run.update(CustomFoodQuery.deleteCustomFood)

  implicit def upsertTargetNutrientsRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Update[F, (UID, Nutrients)] =
    Run.update((UserQuery.upsertTargetNutrients _).tupled)

  implicit def insertDefaultUserRepo[F[_]: MonadCancelThrow: Transactor](
      implicit l: LogHandler): Run.Update[F, (Username, Password)] =
    Run.update((UserQuery.insertDefaultUser _).tupled)
}
