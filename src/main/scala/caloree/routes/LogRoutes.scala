package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder}

import cats.data.EitherT
import cats.syntax.all._
import cats.{MonadThrow, Monoid}

import caloree.dto.ModifyLog
import caloree.model.Types.UID
import caloree.model._
import caloree.query.Run
import caloree.routes.Params.{DateP, Limit, PageP}
import caloree.util._

import java.time.LocalDate

object LogRoutes {

  def routes[F[_]: MonadThrow: EntityDecoder[*[_], ModifyLog]](
      implicit
      get: Run.Many[F, (UID, LocalDate), Log],
      add: Run.Unique[F, (ModifyLog, UID), Int]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? DateP(d) +& PageP(p) +& Limit(l) as u => get.run((u.id, d), p, l).asResponse

      case req @ POST -> _ as u => req.decode.foldF(_.asResponse, v => add.run((v, u.id)) *> Ok())
    }
  }
}