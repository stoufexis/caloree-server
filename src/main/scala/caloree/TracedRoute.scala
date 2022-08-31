package caloree

import caloree.TracedHttpRoute.TracedHttpRoute
import caloree.TracedRoute.Trace.{PathLeaf, PathNode}
import cats.{Monoid, _}
import cats.data.{Kleisli, Writer}
import cats.syntax.all._
import org.http4s.Method.{GET, POST}
import org.http4s.server.Router
import org.http4s.{AuthedRequest, AuthedRoutes, HttpRoutes, Method, Request, Response}

import scala.annotation.tailrec

object TracedRoute {

  type PathComponents = List[String]

  private case class Diff(common: PathComponents, diff: (PathComponents, PathComponents))

  /*
   results in:
   fst = common components
   snd = different components
   */
  private def diff(p1: PathComponents, p2: PathComponents): Diff = {
    @tailrec
    def loop(p1a: PathComponents, p2a: PathComponents, acc: PathComponents): Diff =
      (p1a, p2a) match {
        case (h1 :: t1, h2 :: t2) if h1 == h2 => loop(t1, t2, h1 :: acc)
        case ls                               => Diff(acc.reverse, ls)
      }
    loop(p1, p2, Nil)
  }

  sealed trait Trace

  object Trace {
    case class PathNode(name: PathComponents, sub: List[Trace])                      extends Trace
    case class PathLeaf(method: Method, qParams: List[String], name: PathComponents) extends Trace
    case object Bottom                                                               extends Trace
  }

  implicit val traceShow: Show[Trace] = new Show[Trace] {
    def show(t: Trace): String = {

      def merge(sep: String): (String, String) => String = _ + sep + _

      /*
        case
          PathLeaf -> String representation of PathLeaf
          PathNode -> new backlog with current path as root
          Bottom   -> Returns its root
       */
      def showSingle(tr: (String, Trace)): Either[List[(String, Trace)], String] =
        tr match {
          case (root, Trace.PathNode(n, s)) =>
            val name = n.foldLeft(root)(merge("/"))
            Left(s.map((name, _)))

          case (root, Trace.PathLeaf(m, q, n)) =>
            val name       = n.foldLeft(root)(merge("/"))
            val withParams = q.foldLeft(s"$m -> $name ? ")(merge(" "))
            Right(withParams)

          case (root, Trace.Bottom) => Right(root)
        }

      @tailrec
      def loop(l: List[(String, Trace)], backlog: List[(String, Trace)], acc: List[String]): List[String] =
        l match {
          case head :: tail =>
            showSingle(head) match {
              case Left(value)  => loop(tail, value.concat(backlog), acc)
              case Right(value) => loop(tail, backlog, value :: acc)
            }

          case Nil =>
            backlog match {
              case Nil => acc
              case b   => loop(b, Nil, acc)
            }
        }

      showSingle(("root", t)) match {
        case Left(value)  => loop(value, Nil, Nil).reduce(_ + "\n" + _)
        case Right(value) => value
      }
    }
  }

  implicit val traceMonoid: Monoid[Trace] = new Monoid[Trace] {
    def empty: Trace = Trace.Bottom

    def combine(x: Trace, y: Trace): Trace = (x, y) match {
      case (Trace.PathNode(n1, s1), Trace.PathNode(n2, s2)) =>
        val Diff(common, (d1, d2)) = diff(n1, n2)
        Trace.PathNode(common, List(Trace.PathNode(d1, s1), Trace.PathNode(d2, s2)))

      case (Trace.PathNode(n1, s1), Trace.PathLeaf(m, q, n2)) =>
        val Diff(common, (d1, d2)) = diff(n1, n2)
        Trace.PathNode(common, List(Trace.PathNode(d1, s1), Trace.PathLeaf(m, q, d2)))

      case (Trace.PathLeaf(m, q, n1), Trace.PathNode(n2, s2)) =>
        val Diff(common, (d1, d2)) = diff(n1, n2)
        Trace.PathNode(common, List(Trace.PathLeaf(m, q, d1), Trace.PathNode(d2, s2)))

      case (Trace.PathLeaf(m1, q1, n1), Trace.PathLeaf(m2, q2, n2)) =>
        val Diff(common, (d1, d2)) = diff(n1, n2)
        Trace.PathNode(common, List(Trace.PathLeaf(m1, q1, d1), Trace.PathLeaf(m2, q2, d2)))

      case (t, Trace.Bottom) => t
      case (Trace.Bottom, t) => t
    }
  }


  object Route {
    type TracedMapping[F[_]] = (String, TracedHttpRoute[F])
    type Mapping[F[_]]       = (String, HttpRoutes[F])

    private def traverseTracedMapping[F[_]](m: TracedMapping[F]): Writer[Trace, Mapping[F]] = {
      val (name, trRoute) = m
      val (tr, route)     = trRoute.run
      Writer(Trace.PathNode(List(name), List(tr)), (name, route))
    }

    private def combineAll[F[_]](r1: Seq[Writer[Trace, Mapping[F]]]): Writer[Trace, List[Mapping[F]]] =
      r1.map(_.map(List(_))).combineAll

    def apply[F[_]: Monad](mappings: TracedMapping[F]*): TracedHttpRoute[F] =
      combineAll(mappings.map(traverseTracedMapping)).map(ms => Router.apply(ms: _*))

  }





  def tracedMiddleware[F[_], A, B, C, D](mid: Kleisli[F, A, B] => Kleisli[F, C, D])
      : Writer[Trace, Kleisli[F, A, B]] => Writer[Trace, Kleisli[F, C, D]] = _.map(mid(_))

}

object Main extends App {
  import TracedRoute._

  val tr = PathNode(
    Nil,
    List(
      PathNode(
        List("foods"),
        List(
          PathLeaf(GET, List("page"), List("gett")),
          PathNode(
            List("custom"),
            List(
              PathLeaf(GET, List("page"), List("gett"))
            )),
          PathLeaf(POST, Nil, List("postt"))
        )
      ),
      PathNode(
        List("meals"),
        List(
          PathLeaf(GET, List("page"), List("gett")),
          PathLeaf(POST, Nil, List("postt"))))
    )
  )

  println(Show[Trace].show(tr))

}
