package caloree

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher

import cats.Applicative
import cats.syntax.all._

trait ExtractParams[A] {
  val strings: List[String]
  val extract: Map[String, Seq[String]] => Option[A]
}

object ExtractParams {

  def unapply[A](a: ExtractParams[A]): Option[(List[String], Map[String, Seq[String]] => Option[A])] =
    Some(a.strings, a.extract)

  def apply[A: QueryParamDecoder](p1: String): ExtractParams[A] = new ExtractParams[A] {
    object QA extends QueryParamDecoderMatcher[A](p1)
    val strings: List[String]                          = List(p1)
    val extract: Map[String, Seq[String]] => Option[A] = QA.unapply(_)
  }

  private def product[A, B](o1: Option[A], o2: Option[B]): Option[(A, B)] =
    Applicative[Option].product(o1, o2)

  def apply[A: QueryParamDecoder, B: QueryParamDecoder](p1: String, p2: String): ExtractParams[(A, B)] =
    new ExtractParams[(A, B)] {
      object QA extends QueryParamDecoderMatcher[A](p1)
      object QB extends QueryParamDecoderMatcher[B](p2)

      val strings: List[String]                               = List(p1, p2)
      val extract: Map[String, Seq[String]] => Option[(A, B)] =
        m => product(QA.unapply(m), QB.unapply(m))
    }

  def apply[A: QueryParamDecoder, B: QueryParamDecoder, C: QueryParamDecoder](
      p1: String,
      p2: String,
      p3: String): ExtractParams[(A, B, C)] =
    new ExtractParams[(A, B, C)] {
      object QA extends QueryParamDecoderMatcher[A](p1)
      object QB extends QueryParamDecoderMatcher[B](p2)
      object QC extends QueryParamDecoderMatcher[C](p3)

      val strings: List[String]                                  = List(p1, p2, p3)
      val extract: Map[String, Seq[String]] => Option[(A, B, C)] = {
        m =>
          val ap1 = product(QA.unapply(m), QB.unapply(m))
          product(ap1, QC.unapply(m)).map {
            case ((a, b), c) => (a, b, c)
          }
      }
    }

  def apply[A: QueryParamDecoder, B: QueryParamDecoder, C: QueryParamDecoder, D: QueryParamDecoder](
      p1: String,
      p2: String,
      p3: String,
      p4: String): ExtractParams[(A, B, C, D)] =
    new ExtractParams[(A, B, C, D)] {
      object QA extends QueryParamDecoderMatcher[A](p1)
      object QB extends QueryParamDecoderMatcher[B](p2)
      object QC extends QueryParamDecoderMatcher[C](p3)
      object QD extends QueryParamDecoderMatcher[D](p4)

      val strings: List[String] = List(p1)

      val extract: Map[String, Seq[String]] => Option[(A, B, C, D)] = {
        m =>
          val ap1 = product(QA.unapply(m), QB.unapply(m))
          val ap2 = product(ap1, QC.unapply(m))
          product(ap2, QD.unapply(m)).map {
            case (((a, b), c), d) => (a, b, c, d)
          }
      }
    }
}
