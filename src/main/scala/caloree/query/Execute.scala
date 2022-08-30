package caloree.query

import cats.effect.{IO, MonadCancelThrow}

import doobie.ConnectionIO
import doobie._
import doobie.implicits._

trait Execute[F[_], Params, A] {
  def execute(p: Params): F[A]
}

object Execute {
  def makeIO[F[_]: MonadCancelThrow, Params, A](q: Params => ConnectionIO[A])(
      implicit ta: Transactor[F]): Execute[F, Params, A] =
    q(_).transact(ta)
}
