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
      implicit lh: LogHandler): Run.Optional[F, (Username, AccessToken), User] =
    Run.option((AuthQuery.verifyCredentials _).tupled)

  def getTokenRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, (Username, Password), AccessToken] =
    Run.unique((AuthQuery.getToken _).tupled)

  def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (Description, EntityId[User]), CustomFoodPreview] =
    Run.many { case ((d, u), p, l) => CustomFoodPreviewQuery.customFoodsPreviewByDescription(d, u, p, l) }

  def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood] =
    Run.option((CustomFoodQuery.customFoodById _).tupled)

  def foodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, Description, FoodPreview] =
    Run.many { case (desc, page, limit) => FoodPreviewQuery.foodsPreviewByDescription(desc, page, limit) }

  def foodByIdRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Optional[F, (EntityId[Food], Grams), Food] =
    Run.option((FoodQuery.foodById _).tupled)

  def mealFoodByUserAndDateRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Many[F, (EntityId[User], LocalDate), MealFood] =
    Run.many { case ((u, d), page, limit) => MealFoodQuery.mealFoodByUserAndDate(u, d, page, limit) }

  def mealFoodTransactionRepos[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int] =
    Run.unique((DayInstanceQuery.mealFoodsTransaction _).tupled)

}
