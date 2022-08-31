package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{DecodeFailure, EntityDecoder, EntityEncoder}

import cats.MonadThrow
import cats.syntax.all._

import caloree.TracedAuthedRoute.TracedAuthedRoute
import caloree.TracedRoute.Trace
import caloree.model.Types.{EntityId, Page}
import caloree.model.{MealFood, User}
import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.query.Execute
import caloree.routes.Routes.{DateP, Limit, PageP, localDateQueryParamD}
import caloree.routes.dto.MealFoodPayload
import caloree.util.ToResponseListSyntax
import caloree.{ExtractParams, TracedAuthedRoute}

import java.time.LocalDate

object MealFoodRoutes {
  def routes[F[_]: MonadThrow: EntityEncoder[*[_], MealFoodPayload]: EntityDecoder[*[_], MealFoodPayload]](
      implicit
      get: Execute.Many[F, (EntityId[User], LocalDate), MealFood],
      di: Execute.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int]): TracedAuthedRoute[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    val trace = Trace.PathNode(
      Nil,
      List(
        Trace.PathLeaf(GET, List("page", "date", "limit"), Nil),
        Trace.PathLeaf(POST, Nil, Nil)))

    TracedAuthedRoute.of(trace) {
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
