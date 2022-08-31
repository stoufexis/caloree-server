package caloree.query

import doobie.util.transactor.Transactor

import cats.effect.MonadCancelThrow

import caloree.model.Types.{AccessToken, Description, EntityId, Page, Password, Username}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, MealFood, User}
import caloree.query.DayInstanceQuery.MealWithFoods

import java.time.LocalDate

object Repos {
  implicit def verifyCredentialsRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute.Optional[F, (Username, AccessToken), User] =
    Execute.option((AuthQuery.verifyCredentials _).tupled)

  implicit def getTokenRepo[F[_]: MonadCancelThrow: Transactor]: Execute.Unique[F, (Username, Password), AccessToken] =
    Execute.unique((AuthQuery.getToken _).tupled)

  implicit def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute.Many[F, (Description, EntityId[User]), CustomFoodPreview] =
    Execute.many { case ((desc, u), page, limit) =>
      CustomFoodPreviewQuery.customFoodsPreviewByDescription(desc, u, page, limit)
    }

  implicit def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood] =
    Execute.option((CustomFoodQuery.customFoodById _).tupled)

  implicit def foodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute.Many[F, Description, FoodPreview] =
    Execute.many { case (desc, page, limit) => FoodPreviewQuery.foodsPreviewByDescription(desc, page, limit) }

  implicit def foodByIdRepo[F[_]: MonadCancelThrow: Transactor]: Execute.Optional[F, EntityId[Food], Food] =
    Execute.option(FoodQuery.foodById)

  implicit def mealFoodByUserAndDateRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute.Many[F, (EntityId[User], LocalDate), MealFood] =
    Execute.many { case ((u, d), page, limit) => MealFoodQuery.mealFoodByUserAndDate(u, d, page, limit) }

  implicit def mealFoodTransactionRepos[F[_]: MonadCancelThrow: Transactor]
      : Execute.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int] =
    Execute.unique((DayInstanceQuery.mealFoodsTransaction _).tupled)
}
