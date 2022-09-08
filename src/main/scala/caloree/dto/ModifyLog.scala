package caloree.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import io.circe.generic.extras.Configuration

import cats.effect.kernel.Concurrent

import caloree.model.Types.{EFID, Grams, Minute, MinuteInterval}

import java.time.LocalDate

sealed trait ModifyLog

object ModifyLog {
  import io.circe.generic.extras.auto._

  case class Add(fid: EFID, amount: Grams, day: LocalDate, minute: Minute)                       extends ModifyLog
  case class Remove(fid: Option[EFID], day: LocalDate, minute: MinuteInterval)                   extends ModifyLog
  case class Modify(fid: Option[EFID], newAmount: Grams, day: LocalDate, minute: MinuteInterval) extends ModifyLog
  case class Undo(fid: Option[EFID], day: LocalDate, minute: MinuteInterval, times: Int)         extends ModifyLog

  implicit val jsonDiscriminator: Configuration = Configuration.default.withDiscriminator("t")
  implicit def modifyLogEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, ModifyLog] = jsonOf[F, ModifyLog]
}

//object Main extends App {
//  import io.circe.generic.auto._
//  import io.circe.parser.decode
//
//  println(decode[ModifyLog]("{\"type\": \"Add\" ,\"amount\":200,\"day\":\"2022-09-08\",\"fid\":{\"food_id\":167786},\"minute\":987}"))
//}

//{
//  "fid" : {
//    "food_id" : 1
//  },
//  "amount" : 200.0,
//  "day" : "2022-09-08",
//  "minute" : 987
//}
