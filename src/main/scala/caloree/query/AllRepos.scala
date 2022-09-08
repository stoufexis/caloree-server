package caloree.query

import doobie.LogHandler
import doobie.util.transactor.Transactor

import cats.effect.MonadCancelThrow

import caloree.dto.ModifyLog
import caloree.model.Types.{
  AccessToken,
  CFID,
  Description,
  EFID,
  EntityId,
  FID,
  Grams,
  MinuteInterval,
  Offset,
  Password,
  UID,
  Username
}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, Log, Nutrients, User, UserWithNutrients}

import java.time.LocalDate

object AllRepos {
  implicit def verifyCredentialsRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (Username, Password), User] =
    Run.option((UserQuery.login _).tupled)

  implicit def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (Description, UID), CustomFoodPreview] =
    Run.many { case ((d, u), p, l) => CustomFoodPreviewQuery.customFoodsPreviewByDescription(d, u, p, l) }

  implicit def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (CFID, UID), CustomFood] =
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
      case (ModifyLog.Add(fid, amount, day, minute), user) =>
        LogQuery.insertLog(fid, amount, day, minute, user)

      case (ModifyLog.Remove(fid, day, minute), user) =>
        LogQuery.logDeletion(fid, day, minute, user)

      case (ModifyLog.Modify(fid, newAmount, day, minute), user) =>
        LogQuery.logModification(fid, newAmount, day, minute, user)

      case (ModifyLog.Undo(fid, day, minute, times), user) =>
        LogQuery.undoLog(user, fid, day, minute, times)
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

}
