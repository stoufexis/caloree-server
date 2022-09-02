package caloree.db_init

import doobie.ConnectionIO

import cats.Monad
import cats.effect.kernel.Sync
import cats.syntax.all._

object Init {
  def apply[F[_]: Sync: Monad]: F[ConnectionIO[Unit]] = for {
    insertStaticFoods <- StaticFoods.insertsStaticFoods[F]
    cio               <-
      (for {
        _ <- Tables.all
        _ <- Views.all
        _ <- insertStaticFoods
      } yield ()).pure
  } yield cio
}
