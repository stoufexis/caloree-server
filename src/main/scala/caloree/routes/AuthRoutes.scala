package caloree.routes

import caloree.TracedRoute.{Trace, TracedHttpRoute}
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import cats.Monad
import cats.syntax.all._
import caloree.model.Types.{AccessToken, Password, Username}
import caloree.query.Execute
import caloree.util._

object AuthRoutes {
  def routes[F[_]: Monad](implicit get: Execute.Unique[F, (Username, Password), AccessToken]): TracedHttpRoute[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    TracedHttpRoute.of(Trace.PathLeaf(GET, Nil, Nil)) {
      case req @ GET -> _ =>
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
    }
  }
}
