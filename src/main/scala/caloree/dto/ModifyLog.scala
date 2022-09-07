package caloree.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._

import cats.effect.kernel.Concurrent

import caloree.model.Types.{EFID, Grams, Minute, MinuteInterval}

import java.time.LocalDate

sealed trait ModifyLog

object ModifyLog {
  case class Add(fid: EFID, amount: Grams, day: LocalDate, minute: Minute)                       extends ModifyLog
  case class Remove(fid: Option[EFID], day: LocalDate, minute: MinuteInterval)                   extends ModifyLog
  case class Modify(fid: Option[EFID], newAmount: Grams, day: LocalDate, minute: MinuteInterval) extends ModifyLog
  case class Undo(fid: Option[EFID], day: LocalDate, minute: MinuteInterval, times: Int)         extends ModifyLog

  implicit val jsonDiscriminator: Configuration = Configuration.default.withDiscriminator("type")
  implicit def modifyLogEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, ModifyLog] = jsonOf[F, ModifyLog]
}
