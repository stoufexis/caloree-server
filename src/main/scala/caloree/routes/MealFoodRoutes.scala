package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, DecodeFailure, EntityDecoder, EntityEncoder}

import cats.effect.kernel.Concurrent
import cats.syntax.all._
import cats.{Monad, MonadError, MonadThrow}

import caloree.model.Types.EntityId
import caloree.model.{MealFood, User}
import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.query.Execute
import caloree.routes.Routes.{DateP, Limit, PageP}
import caloree.routes.dto.MealFoodPayload
import caloree.util.ToResponseListSyntax

import java.time.LocalDate

object MealFoodRoutes {
  def routes[F[_]: MonadThrow: EntityEncoder[*[_], MealFoodPayload]: EntityDecoder[*[_], MealFoodPayload]](
      implicit
      get: Execute.Many[F, (EntityId[User], LocalDate), MealFood],
      di: Execute.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int]): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? PageP(page) +& DateP(date) +& Limit(limit) as u =>
        get.execute((u.id, date), page, limit).asResponse

      case req @ POST -> _ as u =>
        req.req
          .as[MealFoodPayload]
          .flatMap { case MealFoodPayload(date, mfs) =>
            di.execute((u.id, date, mfs)).flatMap(Ok(_))
          }
          .recoverWith {
            case e: DecodeFailure => BadRequest(e.getMessage())
          }
    }
  }

}
