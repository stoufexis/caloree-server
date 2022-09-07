package caloree.query

import doobie._
import doobie.implicits._

import cats.effect.MonadCancelThrow

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

  trait Update[F[_], Params] extends Run[F, Params, Unit] {
    def run(p: Params): F[Unit]
  }

  def option[F[_]: MonadCancelThrow: Transactor, P, A](q: P => ConnectionIO[Option[A]]): Run.Optional[F, P, A] =
    q(_).transact(implicitly[Transactor[F]])

  def unique[F[_]: MonadCancelThrow: Transactor, P, A](q: P => ConnectionIO[A]): Run.Unique[F, P, A] =
    q(_).transact(implicitly[Transactor[F]])

  def many[F[_]: MonadCancelThrow: Transactor, P, A](q: (P, Page, Limit) => ConnectionIO[List[A]]): Run.Many[F, P, A] =
    q(_, _, _).transact(implicitly[Transactor[F]])

  def update[F[_]: MonadCancelThrow: Transactor, P](q: P => ConnectionIO[Unit]): Run.Update[F, P] =
    q(_).map(_ => ()).transact(implicitly[Transactor[F]])
}
