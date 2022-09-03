package caloree.db_init

import doobie.ConnectionIO
import doobie.util.update.Update

import cats.effect._
import cats.syntax.all._

import scala.io.Source

object FromFile {
  def apply[F[_]](fileName: String)(implicit S: Sync[F]): F[ConnectionIO[Int]] = {
    val source = Source.fromResource(fileName).pure[F]
    MonadCancel[F]
      .bracket(source)(in => S.blocking(in.getLines().mkString("\n")))(in => S.blocking(in.close().pure))
      .map((s: String) => Update.apply(s).run(()))
  }
}
