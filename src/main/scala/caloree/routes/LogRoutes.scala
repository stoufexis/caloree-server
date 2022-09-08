package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{OptionalQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import org.http4s.{AuthedRoutes, EntityDecoder}

import cats.MonadThrow
import cats.syntax.all._

import caloree.dto.ModifyLog
import caloree.model.Types.{EFID, EntityId, MinuteInterval, Offset, UID}
import caloree.model._
import caloree.query.Run
import caloree.routes.Params.{DateP, LimitP, OffsetP, PageP}
import caloree.util._

import java.time.LocalDate

object LogRoutes {

  object IntervalP extends QueryParamDecoderMatcher[MinuteInterval]("interval")
  object OFIDP     extends OptionalQueryParamDecoderMatcher[EntityId[Food]]("food_id")
  object OCFIDP    extends OptionalQueryParamDecoderMatcher[EntityId[CustomFood]]("custom_food_id")

  def routes[F[_]: MonadThrow: EntityDecoder[*[_], ModifyLog]](
      implicit
      get: Run.Many[F, (UID, Option[EFID], Offset, LocalDate, MinuteInterval), Log],
      add: Run.Update[F, (ModifyLog, UID)]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? OFIDP(fid) +& DateP(d) +& PageP(p) +& LimitP(l) +& OffsetP(offset) +& IntervalP(mi) as u =>
        get.run((u.id, fid.map(Right(_)), offset, d, mi), p, l).asResponse

      case GET -> _ :? OCFIDP(cfid) +& DateP(d) +& PageP(p) +& LimitP(l) +& OffsetP(offset) +& IntervalP(mi) as u =>
        get.run((u.id, cfid.map(Left(_)), offset, d, mi), p, l).asResponse

      case req @ POST -> _ as u =>
        req.decode.foldF(_.asResponse, v => add.run((v, u.id)) *> Ok())
    }
  }
}
