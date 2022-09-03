package caloree.model

import doobie.{Get, Put}

import org.http4s.QueryParamDecoder

import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder}
import io.estatico.newtype.macros.newtype

import java.time.LocalDate

object Types {
  @newtype case class EntityId[A](toLong: Long)
  @newtype case class Page(toFloat: Float)
  @newtype case class Grams(toFloat: Float)
  @newtype case class Kcal(toFloat: Float)
  @newtype case class Description(string: String)
  @newtype case class Username(string: String)
  @newtype case class AccessToken(string: String)
  @newtype case class Password(string: String)
  @newtype case class Minute(string: String)

  type UID = EntityId[User]
  type FID = Either[EntityId[CustomFood], EntityId[Food]]

  implicit val eitherIdsDecoder: CirceDecoder[FID] = CirceDecoder.decodeEither("custom_food_id", "food_id")
  implicit val eitherIdsEncoder: CirceEncoder[FID] = CirceEncoder.encodeEither("custom_food_id", "food_id")

  object Minute {
    implicit val putMinute: Put[Minute]                   = deriving
    implicit val getMinute: Get[Minute]                   = deriving
    implicit val minuteCirceEncoder: CirceEncoder[Minute] = deriving
    implicit val minuteCirceDecoder: CirceDecoder[Minute] = deriving
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
