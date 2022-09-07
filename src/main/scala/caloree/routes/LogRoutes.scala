package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder}
import cats.MonadThrow
import cats.syntax.all._
import caloree.dto.ModifyLog
import caloree.model.Types.{Limit, MinuteInterval, Offset, UID}
import caloree.model._
import caloree.query.Run
import caloree.routes.Params.{DateP, LimitP, OffsetP, PageP}
import caloree.util._
import org.http4s.dsl.impl.QueryParamDecoderMatcher

import java.time.LocalDate

object LogRoutes {

  object IntervalP extends QueryParamDecoderMatcher[MinuteInterval]("interval")

  def routes[F[_]: MonadThrow: EntityDecoder[*[_], ModifyLog]](
      implicit
      get: Run.Many[F, (UID, Offset, LocalDate, MinuteInterval), Log],
      add: Run.Update[F, (ModifyLog, UID)]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? DateP(d) +& PageP(p) +& LimitP(l) +& OffsetP(offset) +& IntervalP(mi) as u =>
        get.run((u.id, offset, d, mi), p, l).asResponse

      case req @ POST -> _ as u =>
        req.decode.foldF(_.asResponse, v => add.run((v, u.id)) *> Ok())
    }
  }
}
