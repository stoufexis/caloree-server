package caloree.query

import doobie.LogHandler
import doobie.util.transactor.Transactor

import cats.effect.MonadCancelThrow

import caloree.model.Types.{AccessToken, Description, EntityId, Grams, Page, Password, Username}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, Meal, MealFood, User}
import caloree.query.MealFoodQuery.{CustomFoodAndMealParams, FoodAndMealParams}

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
      implicit lh: LogHandler): Run.Many[F, (EntityId[User], LocalDate), MealFood] =
    Run.many { case ((u, d), page, limit) => MealFoodQuery.mealFoodByUserAndDate(u, d, page, limit) }

  implicit def mealFoodRepos[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, (EntityId[Food], EntityId[Meal], Grams), Int] =
    Run.unique((MealFoodQuery.insertMealFood _).tupled)

  implicit def mealCustomFoodRepos[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, (EntityId[CustomFood], EntityId[Meal], Grams, EntityId[User]), Int] =
    Run.unique((MealFoodQuery.insertMealCustomFood _).tupled)

  implicit def insertFoodAndMealRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, FoodAndMealParams, Int] =
    Run.unique((MealFoodQuery.insertFoodAndMeal _).tupled)

  implicit def insertCustomFoodAndMealRepo[F[_]: MonadCancelThrow: Transactor](
      implicit lh: LogHandler): Run.Unique[F, CustomFoodAndMealParams, Int] =
    Run.unique((MealFoodQuery.insertCustomFoodAndMeal _).tupled)

}
