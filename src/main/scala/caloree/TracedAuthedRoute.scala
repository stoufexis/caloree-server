package caloree

import org.http4s.Method.{GET, POST}
import org.http4s.server.Router
import org.http4s.{AuthedRequest, AuthedRoutes, HttpRoutes, Method, Request, Response}

import cats.data.{Kleisli, Writer}
import cats.syntax.all._
import cats.{Monoid, _}

import caloree.ExtractParams
import caloree.TracedRoute.Trace.{PathLeaf, PathNode}

import TracedRoute._

//noinspection DuplicatedCode
object TracedAuthedRoute {
  type TracedAuthedRoute[U, F[_]] = Writer[Trace, AuthedRoutes[U, F]]

  type Route[F[_], U, A] = ((Method, ExtractParams[F, A]), PartialFunction[(Request[F], A, U), F[Response[F]]])

  def of[U, F[_]: Monad](trace: Trace)(pf: PartialFunction[AuthedRequest[F, U], F[Response[F]]])
      : TracedAuthedRoute[U, F] = Writer(trace, AuthedRoutes.of(pf))

  def route[A, U, F[_]: Monad](r: Route[F, U, A]): TracedAuthedRoute[U, F] = {
    val ((method, ext), f)   = r
    val ExtractParams(qs, q) = ext
    val trace                = Trace.PathLeaf(method, qs, Nil)

    of(trace)(decode[F, A, U](q) andThen partialOption andThen f)
  }

  def route2[A, B, U, F[_]: Monad](r1: Route[F, U, A], r2: Route[F, U, B]): TracedAuthedRoute[U, F] = {
    val ((method1, ext1), f1)                            = r1
    val ((method2, ext2), f2)                            = r2
    val (ExtractParams(qs1, q1), ExtractParams(qs2, q2)) = (ext1, ext2)

    val trace =
      Trace.PathNode(Nil, List(Trace.PathLeaf(method1, qs1, Nil), Trace.PathLeaf(method2, qs2, Nil)))

    val routes =
      (decode[F, A, U](q1) andThen partialOption andThen f1) orElse
        (decode[F, B, U](q2) andThen partialOption andThen f2)

    of(trace)(routes)
  }

  private def partialOption[A, U, F[_]]: PartialFunction[(Request[F], Option[A], U), (Request[F], A, U)] = {
    case (r, Some(a), u) => (r, a, u)
  }

  private def decode[F[_], A, U](q: Request[F] => Option[A])
      : PartialFunction[AuthedRequest[F, U], (Request[F], Option[A], U)] =
    PartialFunction.fromFunction(req => (req.req, q(req.req), req.context))
}
