package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl

import cats.Monad
import cats.syntax.all._

import caloree.TracedHttpRoute.{Route, TracedHttpRoute}
import caloree.TracedRoute.Trace
import caloree.model.Types.{AccessToken, Description, Password, Username}
import caloree.query.Execute
import caloree.util._
import caloree.{ExtractParams, TracedHttpRoute}

object AuthRoutes {
  def routes[F[_]: Monad](implicit get: Execute.Unique[F, (Username, Password), AccessToken]): TracedHttpRoute[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    val r1: Route[F, ExtractParams.Void.type] = (
      (GET, ExtractParams.void),
      { case (req, _) =>
        for {
          headers <-
            extractHeaders(req.headers, ("USER-ID", "PASS"), Username(_), Password(_))
              .map(get.execute)
              .traverse(identity)

          response <- headers match {
            case Some(value) => Ok(value)
            case None        => NotFound("Not Found")
          }
        } yield response
      })

    TracedHttpRoute.route(r1)
  }
}
