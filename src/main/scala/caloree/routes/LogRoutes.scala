package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder}

import cats.MonadThrow
import cats.syntax.all._

import caloree.dto.ModifyLog
import caloree.model.Types.{EntityId, UID}
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
      case GET -> _ :? DateP(date) +& PageP(page) +& Limit(limit) as u =>
        get.run((u.id, date), page, limit).asResponse

      case req @ POST -> _ as u =>
        req.as[ModifyLog].map((_, u.id)).flatMap(add.run) *> Ok()
    }
  }
}
