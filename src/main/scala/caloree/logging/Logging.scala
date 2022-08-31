package caloree.logging

import org.http4s.HttpRoutes

import cats.data.Kleisli
import cats.syntax.all._
import cats.{MonadError, MonadThrow, ~>}

object Logging {
  def apply[F[_]: MonadThrow](service: HttpRoutes[F]): HttpRoutes[F] =
    Kleisli { req =>
      val log = λ[F ~> F](_.onError { case e: Exception => println(e).pure[F] })
      service(req).mapK(log)
    }

}
