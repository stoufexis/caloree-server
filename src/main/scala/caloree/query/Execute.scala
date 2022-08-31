package caloree.query

import doobie.ConnectionIO
import doobie._
import doobie.implicits._

import cats.effect.{IO, MonadCancelThrow}

import caloree.model.Types.Page

sealed trait Execute[F[_], Params, A]

object Execute {

  trait Optional[F[_], Params, A] extends Execute[F, Params, A] {
    def execute(p: Params): F[Option[A]]
  }

  trait Unique[F[_], Params, A] extends Execute[F, Params, A] {
    def execute(p: Params): F[A]
  }

  trait Many[F[_], Params, A] extends Execute[F, Params, A] {
    def execute(p: Params, page: Page, limit: Int): F[List[A]]
  }

  def option[F[_]: MonadCancelThrow: Transactor, P, A](
      q: P => ConnectionIO[Option[A]]): Execute.Optional[F, P, A] =
    q(_).transact(implicitly[Transactor[F]])

  def unique[F[_]: MonadCancelThrow: Transactor, P, A](
      q: P => ConnectionIO[A]): Execute.Unique[F, P, A] =
    q(_).transact(implicitly[Transactor[F]])

  def many[F[_]: MonadCancelThrow: Transactor, P, A](
      q: (P, Page, Int) => ConnectionIO[List[A]]): Execute.Many[F, P, A] =
    q(_, _, _).transact(implicitly[Transactor[F]])

}
