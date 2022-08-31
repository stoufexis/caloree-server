package caloree

import org.http4s.Method.{GET, POST}
import org.http4s.server.Router
import org.http4s.{AuthedRequest, AuthedRoutes, HttpRoutes, Method, Request, Response}

import cats.data.{Kleisli, Writer}
import cats.syntax.all._
import cats.{Monoid, _}

import caloree.TracedRoute.Trace.{PathLeaf, PathNode}

import TracedRoute._

object TracedHttpRoute {
  type TracedHttpRoute[F[_]] = Writer[Trace, HttpRoutes[F]]

  type Route[F[_], A] = (Method, ExtractParams[A], PartialFunction[A, F[Response[F]]])

  def of[F[_]: Monad](trace: Trace)(pf: PartialFunction[Request[F], F[Response[F]]]): TracedHttpRoute[F] =
    Writer(trace, HttpRoutes.of(pf))

  def route[A, F[_]: Monad](r1: Route[F, A]): TracedHttpRoute[F] = {
    val (method, ext, f)     = r1
    val ExtractParams(qs, q) = ext
    val trace                = Trace.PathLeaf(method, qs, Nil)
    val route                = decode[F, A](q) andThen partialOption andThen f
    of(trace)(route)
  }

  def route2[A, B, F[_]: Monad](r1: Route[F, A])(r2: Route[F, B]): TracedHttpRoute[F] = {
    val (method1, ext1, f1)                              = r1
    val (method2, ext2, f2)                              = r2
    val (ExtractParams(qs1, q1), ExtractParams(qs2, q2)) = (ext1, ext2)

    val trace =
      Trace.PathNode(Nil, List(Trace.PathLeaf(method1, qs1, Nil), Trace.PathLeaf(method2, qs2, Nil)))

    val routes =
      (decode[F, A](q1) andThen partialOption andThen f1) orElse
        (decode[F, B](q2) andThen partialOption andThen f2)

    of(trace)(routes)
  }

  private def partialOption[A]: PartialFunction[Option[A], A] = { case Some(a) => a }

  private def decode[F[_], A](q: Map[String, Seq[String]] => Option[A]): PartialFunction[Request[F], Option[A]] =
    PartialFunction.fromFunction(req => q(req.multiParams))
}
