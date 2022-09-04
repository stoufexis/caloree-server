package caloree.query

import doobie.ConnectionIO
import doobie._
import doobie.implicits._

import cats.effect.{IO, MonadCancelThrow}

import caloree.model.Types.{Limit, Page}

sealed trait Run[F[_], Params, A]

object Run {

  trait Optional[F[_], Params, A] extends Run[F, Params, A] {
    def run(p: Params): F[Option[A]]
  }

  trait Unique[F[_], Params, A] extends Run[F, Params, A] {
    def run(p: Params): F[A]
  }

  trait Many[F[_], Params, A] extends Run[F, Params, A] {
    def run(p: Params, page: Page, limit: Limit): F[List[A]]
  }

  def option[F[_]: MonadCancelThrow: Transactor, P, A](
      q: P => ConnectionIO[Option[A]]): Run.Optional[F, P, A] =
    q(_).transact(implicitly[Transactor[F]])

  def unique[F[_]: MonadCancelThrow: Transactor, P, A](
      q: P => ConnectionIO[A]): Run.Unique[F, P, A] =
    q(_).transact(implicitly[Transactor[F]])

  def many[F[_]: MonadCancelThrow: Transactor, P, A](
      q: (P, Page, Limit) => ConnectionIO[List[A]]): Run.Many[F, P, A] =
    q(_, _, _).transact(implicitly[Transactor[F]])

}
