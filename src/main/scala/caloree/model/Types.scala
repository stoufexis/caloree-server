package caloree.model

import doobie.{Get, Put}

import org.http4s.{ParseFailure, QueryParamDecoder, QueryParameterValue}

import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}
import io.estatico.newtype.macros.newtype

import cats.syntax.all._

object Types {
  @newtype case class EntityId[A](toLong: Long)
  @newtype case class Page(toFloat: Float)
  @newtype case class Grams(toFloat: Float)
  @newtype case class Kcal(toFloat: Float)
  @newtype case class Description(string: String)
  @newtype case class Username(string: String)
  @newtype case class AccessToken(string: String)
  @newtype case class Password(string: String)
  @newtype case class Minute(toInt: Int)
  @newtype case class Limit(toInt: Int)
  @newtype case class Offset(toInt: Int)

  case class MinuteInterval(start: Minute, end: Minute)

  object MinuteInterval {
    implicit val queryParamDInterval: QueryParamDecoder[MinuteInterval] = {
      case QueryParameterValue(value) =>
        value.split("-").toList match {
          case start :: end :: Nil =>
            (start.toIntOption, end.toIntOption)
              .mapN { (s, e) => MinuteInterval(Minute(s), Minute(e)) }
              .toValidNel(ParseFailure(value, ""))

          case _ => ParseFailure(value, "").invalidNel
        }
    }
  }

  type UID = EntityId[User]

  type CFID = EntityId[CustomFood]
  object CFID {
    def unapply(string: String): Option[CFID] =
      string.toLongOption.map(EntityId(_))
  }

  type FID = EntityId[Food]
  object FID {
    def unapply(string: String): Option[FID] =
      string.toLongOption.map(EntityId(_))
  }

  type EFID = Either[EntityId[CustomFood], EntityId[Food]]

  implicit val eitherIdsDecoder: CirceDecoder[EFID] = CirceDecoder.decodeEither("custom_food_id", "food_id")
  implicit val eitherIdsEncoder: CirceEncoder[EFID] = CirceEncoder.encodeEither("custom_food_id", "food_id")

  object Minute {
    implicit val putMinute: Put[Minute]                   = deriving
    implicit val getMinute: Get[Minute]                   = deriving
    implicit val minuteCirceEncoder: CirceEncoder[Minute] = deriving
    implicit val minuteCirceDecoder: CirceDecoder[Minute] = deriving
  }

  object Offset {
    implicit val putOffset: Put[Offset]                       = deriving
    implicit val getOffset: Get[Offset]                       = deriving
    implicit val circeEncoderOffset: CirceEncoder[Offset]     = deriving
    implicit val circeDecoderOffset: CirceDecoder[Offset]     = deriving
    implicit val queryParamDOffset: QueryParamDecoder[Offset] = deriving
  }

  object Limit {
    implicit val putLimit: Put[Limit]                       = deriving
    implicit val getLimit: Get[Limit]                       = deriving
    implicit val circeEncoderLimit: CirceEncoder[Limit]     = deriving
    implicit val circeDecoderLimit: CirceDecoder[Limit]     = deriving
    implicit val queryParamDLimit: QueryParamDecoder[Limit] = deriving
  }

  object EntityId {
    implicit def putPage[A]: Put[EntityId[A]]                           = deriving
    implicit def getPage[A]: Get[EntityId[A]]                           = deriving
    implicit def entityIdQueryParamD[A]: QueryParamDecoder[EntityId[A]] = deriving
    implicit def entityIdCirceEncoder[A]: CirceEncoder[EntityId[A]]     = deriving
    implicit def entityIdCirceDecoder[A]: CirceDecoder[EntityId[A]]     = deriving
  }

  object Page {
    implicit val putPage: Put[Page]                       = deriving
    implicit val getPage: Get[Page]                       = deriving
    implicit val pageQueryParamD: QueryParamDecoder[Page] = deriving
    implicit val pageCirceEncoder: CirceEncoder[Page]     = deriving
    implicit val pageCirceDecoder: CirceDecoder[Page]     = deriving
  }

  object Description {
    implicit val putDescription: Put[Description]                = deriving
    implicit val getDescription: Get[Description]                = deriving
    implicit val DescQueryParamD: QueryParamDecoder[Description] = deriving
    implicit val pageCirceEncoder: CirceEncoder[Description]     = deriving
    implicit val pageCirceDecoder: CirceDecoder[Description]     = deriving
  }

  object Kcal {
    implicit val putKcal: Put[Kcal]                   = deriving
    implicit val getKcal: Get[Kcal]                   = deriving
    implicit val kcalCirceEncoder: CirceEncoder[Kcal] = deriving
    implicit val kcalCirceDecoder: CirceDecoder[Kcal] = deriving
  }

  object Grams {
    implicit val putGrams: Put[Grams]                      = deriving
    implicit val getGrams: Get[Grams]                      = deriving
    implicit val DescQueryParamD: QueryParamDecoder[Grams] = deriving
    implicit val gramsCirceEncoder: CirceEncoder[Grams]    = deriving
    implicit val gramsCirceDecoder: CirceDecoder[Grams]    = deriving
  }

  object Username {
    implicit val putUsername: Put[Username]                = deriving
    implicit val getUsername: Get[Username]                = deriving
    implicit val gramsCirceEncoder: CirceEncoder[Username] = deriving
    implicit val gramsCirceDecoder: CirceDecoder[Username] = deriving
  }

  object AccessToken {
    implicit val putAccessToken: Put[AccessToken]             = deriving
    implicit val getAccessToken: Get[AccessToken]             = deriving
    implicit val gramsCirceEncoder: CirceEncoder[AccessToken] = deriving
    implicit val gramsCirceDecoder: CirceDecoder[AccessToken] = deriving
  }

  object Password {
    implicit val putPassword: Put[Password] = deriving
    implicit val getPassword: Get[Password] = deriving
  }
}
