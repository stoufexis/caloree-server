package caloree

import cats.syntax.all._
import cats.{Monad, MonadThrow}
import io.circe.syntax.EncoderOps
import io.circe.{Encoder => CirceEncoder}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, EntityDecoder, Headers, Response}
import org.typelevel.ci.CIString

package object util {
  def extractHeaders[A, B](
      headers: Headers,
      names: (String, String),
      f1: String => A,
      f2: String => B
  ): Option[(A, B)] = for {
    hs1 <- headers.get(CIString(names._1))
    hs2 <- headers.get(CIString(names._2))
    h1 = hs1.head.value
    h2 = hs2.head.value
  } yield (f1(h1), f2(h2))

  implicit class ToResponseOptionSyntax[F[_]: Monad, A: CirceEncoder](o: F[Option[A]]) {
    private val dsl: Http4sDsl[F] = Http4sDsl[F]
    import dsl._

    def asResponse: F[Response[F]] = o.flatMap {
      case Some(value) => Ok(value.asJson)
      case None        => NotFound()
    }
  }

  implicit class ToResponseListSyntax[F[_]: Monad, A: CirceEncoder](o: F[List[A]]) {
    private val dsl: Http4sDsl[F] = Http4sDsl[F]
    import dsl._

    def asResponse: F[Response[F]] = o.map(_.asJson).flatMap(Ok(_))
  }

  implicit class CondSyntax(p: Boolean) {
    def cond[A, B](right: => A, left: => B): Either[B, A] =
      Either.cond(p, right, left)
  }

  implicit class DecodeAuthedRequestSyntax[U, F[_]: MonadThrow](req: AuthedRequest[F, U]) {
    def as[A](implicit enc: EntityDecoder[F, A]): F[A] = req.req.as[A]
  }
}
