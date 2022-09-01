package caloree.trace

import caloree.trace.TracedRoute.Trace
import org.http4s.{HttpRoutes, Method, Request, Response}
import cats.Monad
import cats.data.Writer

object TracedHttpRoute {
  type TracedHttpRoute[F[_]] = Writer[Trace, HttpRoutes[F]]

  type Route[F[_], A] = ((Method, ExtractParams[F, A]), PartialFunction[(Request[F], A), F[Response[F]]])

  def of[F[_]: Monad](trace: Trace)(pf: PartialFunction[Request[F], F[Response[F]]]): TracedHttpRoute[F] =
    Writer(trace, HttpRoutes.of(pf))

  def route[A, F[_]: Monad](r1: Route[F, A]): TracedHttpRoute[F] = {
    val ((method, ext), f)   = r1
    val ExtractParams(qs, q) = ext
    val trace                = Trace.PathLeaf(method, qs, Nil)
    val route                = decode[F, A](q) andThen partialOption andThen f
    of(trace)(route)
  }

  def route2[A, B, F[_]: Monad](r1: Route[F, A])(r2: Route[F, B]): TracedHttpRoute[F] = {
    val ((method1, ext1), f1)                            = r1
    val ((method2, ext2), f2)                            = r2
    val (ExtractParams(qs1, q1), ExtractParams(qs2, q2)) = (ext1, ext2)

    val trace =
      Trace.PathNode(Nil, List(Trace.PathLeaf(method1, qs1, Nil), Trace.PathLeaf(method2, qs2, Nil)))

    val routes =
      (decode[F, A](q1) andThen partialOption andThen f1) orElse
        (decode[F, B](q2) andThen partialOption andThen f2)

    of(trace)(routes)
  }

  private def partialOption[F[_], A]: PartialFunction[(Request[F], Option[A]), (Request[F], A)] = {
    case (r, Some(a)) => (r, a)
  }

  private def decode[F[_], A](q: Request[F] => Option[A]): PartialFunction[Request[F], (Request[F], Option[A])] =
    PartialFunction.fromFunction(req => (req, q(req)))
}
