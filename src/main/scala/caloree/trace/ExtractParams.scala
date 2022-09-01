package caloree.trace

import cats.Applicative
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.{QueryParamDecoder, Request}

sealed trait ExtractParams[F[_], A] {
  val strings: List[String]
  val extract: Request[F] => Option[A]
}

object ExtractParams {
  object Void

  def void[F[_]]: ExtractParams[F, ExtractParams.Void.type] = new ExtractParams[F, ExtractParams.Void.type] {
    val strings: List[String]                                  = Nil
    val extract: Request[F] => Option[ExtractParams.Void.type] = _ => Some(ExtractParams.Void)
  }

  def unapply[F[_], A](a: ExtractParams[F, A]): Option[(List[String], Request[F] => Option[A])] =
    Some(a.strings, a.extract)

  def apply[F[_], A: QueryParamDecoder](p1: String): ExtractParams[F, A] = new ExtractParams[F, A] {
    object QA extends QueryParamDecoderMatcher[A](p1)
    val strings: List[String]            = List(p1)
    val extract: Request[F] => Option[A] = req => QA.unapply(req.multiParams)
  }

  private def product[A, B](o1: Option[A], o2: Option[B]): Option[(A, B)] =
    Applicative[Option].product(o1, o2)

  def apply[F[_], A: QueryParamDecoder, B: QueryParamDecoder](p1: String, p2: String): ExtractParams[F, (A, B)] =
    new ExtractParams[F, (A, B)] {
      object QA extends QueryParamDecoderMatcher[A](p1)
      object QB extends QueryParamDecoderMatcher[B](p2)
      val strings: List[String]                 = List(p1, p2)
      val extract: Request[F] => Option[(A, B)] =
        m => product(QA.unapply(m.multiParams), QB.unapply(m.multiParams))
    }

  def apply[F[_], A: QueryParamDecoder, B: QueryParamDecoder, C: QueryParamDecoder](
      p1: String,
      p2: String,
      p3: String): ExtractParams[F, (A, B, C)] =
    new ExtractParams[F, (A, B, C)] {
      object QA extends QueryParamDecoderMatcher[A](p1)
      object QB extends QueryParamDecoderMatcher[B](p2)
      object QC extends QueryParamDecoderMatcher[C](p3)
      val strings: List[String]                    = List(p1, p2, p3)
      val extract: Request[F] => Option[(A, B, C)] = {
        m =>
          val ap1 = product(QA.unapply(m.multiParams), QB.unapply(m.multiParams))
          product(ap1, QC.unapply(m.multiParams)).map {
            case ((a, b), c) => (a, b, c)
          }
      }
    }

  def apply[F[_], A: QueryParamDecoder, B: QueryParamDecoder, C: QueryParamDecoder, D: QueryParamDecoder](
      p1: String,
      p2: String,
      p3: String,
      p4: String): ExtractParams[F, (A, B, C, D)] =
    new ExtractParams[F, (A, B, C, D)] {
      object QA extends QueryParamDecoderMatcher[A](p1)
      object QB extends QueryParamDecoderMatcher[B](p2)
      object QC extends QueryParamDecoderMatcher[C](p3)
      object QD extends QueryParamDecoderMatcher[D](p4)
      val strings: List[String]                       = List(p1)
      val extract: Request[F] => Option[(A, B, C, D)] = {
        m =>
          val ap1 = product(QA.unapply(m.multiParams), QB.unapply(m.multiParams))
          val ap2 = product(ap1, QC.unapply(m.multiParams))
          product(ap2, QD.unapply(m.multiParams)).map {
            case (((a, b), c), d) => (a, b, c, d)
          }
      }
    }
}
