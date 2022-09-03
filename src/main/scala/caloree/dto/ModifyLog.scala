package caloree.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import cats.effect.kernel.Concurrent
import caloree.model.Types.{FID, Grams, Minute}

import java.time.LocalDate

sealed trait ModifyLog

object ModifyLog {
  case class Add(fid: FID, amount: Grams, day: LocalDate, minute: Minute)       extends ModifyLog
  case class Remove(fid: FID, day: LocalDate, minute: Minute)                   extends ModifyLog
  case class Modify(fid: FID, newAmount: Grams, day: LocalDate, minute: Minute) extends ModifyLog

  implicit val jsonDiscriminator: Configuration = Configuration.default.withDiscriminator("type")
  implicit def modifyLogEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, ModifyLog] = jsonOf[F, ModifyLog]
}
