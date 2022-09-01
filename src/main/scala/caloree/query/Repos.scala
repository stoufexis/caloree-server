package caloree.query

import doobie.LogHandler
import doobie.util.transactor.Transactor

import cats.effect.MonadCancelThrow

import caloree.model.Types.{AccessToken, Description, EntityId, Grams, Page, Password, Username}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, MealFood, User}
import caloree.query.DayInstanceQuery.MealWithFoods

import java.time.LocalDate

object Repos {
  def verifyCredentialsRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Optional[F, (Username, AccessToken), User] =
    Execute.option((AuthQuery.verifyCredentials _).tupled)

  def getTokenRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Unique[F, (Username, Password), AccessToken] =
    Execute.unique((AuthQuery.getToken _).tupled)

  def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Many[F, (Description, EntityId[User]), CustomFoodPreview] =
    Execute.many { case ((d, u), p, l) => CustomFoodPreviewQuery.customFoodsPreviewByDescription(d, u, p, l) }

  def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood] =
    Execute.option((CustomFoodQuery.customFoodById _).tupled)

  def foodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Many[F, Description, FoodPreview] =
    Execute.many { case (desc, page, limit) => FoodPreviewQuery.foodsPreviewByDescription(desc, page, limit) }

  def foodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Optional[F, (EntityId[Food], Grams), Food] =
    Execute.option((FoodQuery.foodById _).tupled)

  def mealFoodByUserAndDateRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Many[F, (EntityId[User], LocalDate), MealFood] =
    Execute.many { case ((u, d), page, limit) => MealFoodQuery.mealFoodByUserAndDate(u, d, page, limit) }

  def mealFoodTransactionRepos[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Execute.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int] =
    Execute.unique((DayInstanceQuery.mealFoodsTransaction _).tupled)

}
