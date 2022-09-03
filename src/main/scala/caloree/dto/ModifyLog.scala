package caloree.dto

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import cats.effect.kernel.Concurrent
import caloree.model.Types.{Description, EntityId, Grams}
import caloree.model.{CustomFood, Food, User}
import cats.effect.IO
import io.circe.Encoder

import java.time.LocalDate

sealed trait ModifyLog

object ModifyLog {
  case class Add(
      fid: Either[EntityId[CustomFood], EntityId[Food]],
      amount: Grams,
      day: LocalDate,
      quarter: Int,
  ) extends ModifyLog

  case class Remove(
      fid: Either[EntityId[CustomFood], EntityId[Food]],
      day: LocalDate,
      quarter: Int,
  ) extends ModifyLog

  case class Modify(
      fid: Either[EntityId[CustomFood], EntityId[Food]],
      newAmount: Grams,
      day: LocalDate,
      quarter: Int,
  ) extends ModifyLog

  implicit val jsonDiscriminator: Configuration = Configuration.default.withDiscriminator("type")
  implicit def modifyLogEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, ModifyLog] = jsonOf[F, ModifyLog]
}
