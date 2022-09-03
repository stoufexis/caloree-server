package caloree.query

import doobie.LogHandler
import doobie.util.transactor.Transactor

import cats.effect.MonadCancelThrow

import caloree.dto.ModifyLog
import caloree.model.Types.{AccessToken, Description, EntityId, Grams, Password, Username}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, Log, User}

import java.time.LocalDate

object AllRepos {
  implicit def verifyCredentialsRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (Username, AccessToken), User] =
    Run.option((AuthQuery.verifyCredentials _).tupled)

  implicit def getTokenRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, (Username, Password), AccessToken] =
    Run.unique((AuthQuery.getToken _).tupled)

  implicit def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (Description, EntityId[User]), CustomFoodPreview] =
    Run.many { case ((d, u), p, l) => CustomFoodPreviewQuery.customFoodsPreviewByDescription(d, u, p, l) }

  implicit def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood] =
    Run.option((CustomFoodQuery.customFoodById _).tupled)

  implicit def foodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, Description, FoodPreview] =
    Run.many { case (desc, page, limit) => FoodPreviewQuery.foodsPreviewByDescription(desc, page, limit) }

  implicit def foodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (EntityId[Food], Grams), Food] =
    Run.option((FoodQuery.foodById _).tupled)

  implicit def mealFoodByUserAndDateRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (EntityId[User], LocalDate), Log] =
    Run.many { case ((u, d), page, limit) => MealFoodQuery.logByUserAndDate(u, d, page, limit) }

  type InsertMealFoodParams = (Either[EntityId[CustomFood], EntityId[Food]], Grams, LocalDate, Int, EntityId[User])

  implicit def insertMealFoodRepos[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, (ModifyLog, EntityId[User]), Int] =
    Run.unique {
      case (ModifyLog.Add(fid, amount, day, quarter), user) =>
        MealFoodQuery.insertLog(fid, amount, day, quarter, user)

      case (ModifyLog.Remove(fid, day, quarter), user) =>
        MealFoodQuery.logDeletion(fid, day, quarter, user)

      case (ModifyLog.Modify(fid, newAmount, day, quarter), user) =>
        MealFoodQuery.logModification(fid, newAmount, day, quarter, user)
    }

}
