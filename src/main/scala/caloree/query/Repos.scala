package caloree.query

import cats.effect.MonadCancelThrow

import caloree.model.Types.{AccessToken, Description, EntityId, Page, Password, Username}
import caloree.model.{CustomFood, CustomFoodPreview, Food, FoodPreview, MealFood, User}

import java.time.LocalDate

import doobie.util.transactor.Transactor

object Repos {
  implicit def verifyCredentialsRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute[F, (Username, AccessToken), Option[User]] =
    Execute.makeIO((AuthQuery.verifyCredentials _).tupled)

  implicit def getTokenRepo[F[_]: MonadCancelThrow: Transactor]: Execute[F, (Username, Password), AccessToken] =
    Execute.makeIO((AuthQuery.getToken _).tupled)

  implicit def customFoodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute[F, (Description, EntityId[User], Page, Int), List[CustomFoodPreview]] =
    Execute.makeIO((CustomFoodPreviewQuery.customFoodsPreviewByDescription _).tupled)

  implicit def customFoodByIdRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute[F, (EntityId[CustomFood], EntityId[User]), Option[CustomFood]] =
    Execute.makeIO((CustomFoodQuery.customFoodById _).tupled)

  implicit def foodsPreviewByDescriptionRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute[F, (Description, Page, Int), List[FoodPreview]] =
    Execute.makeIO((FoodPreviewQuery.foodsPreviewByDescription _).tupled)

  implicit def foodByIdRepo[F[_]: MonadCancelThrow: Transactor]: Execute[F, EntityId[Food], Option[Food]] =
    Execute.makeIO(FoodQuery.foodById)

  implicit def mealFoodByUserAndDateRepo[F[_]: MonadCancelThrow: Transactor]
      : Execute[F, (EntityId[User], LocalDate, Page, Int), List[MealFood]] =
    Execute.makeIO((MealFoodQuery.mealFoodByUserAndDate _).tupled)
}
