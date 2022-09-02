package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

import cats.effect.kernel.Concurrent
import cats.syntax.all._

import caloree.model.Types.{EntityId, Grams}
import caloree.model._
import caloree.query.AllRepos.InsertMealFoodParams
import caloree.query.Run
import caloree.routes.Params.{DateP, Limit, PageP}
import caloree.util._

import java.time.LocalDate

object MealFoodRoutes {
  def routes[F[_]: Concurrent](
      implicit
      get: Run.Many[F, (EntityId[User], LocalDate), Log],
      add: Run.Unique[F, InsertMealFoodParams, Int]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? DateP(date) +& PageP(page) +& Limit(limit) as u =>
        get.run((u.id, date), page, limit).asResponse

//      case req @ POST -> _ as u =>
//        req.req
//          .as[MealFoodDto]
//          .flatMap {
//            case MealFoodDto(Right(fid), amount, Left(mid), _) => addF.run((fid, mid, amount))
//            case MealFoodDto(Left(cfid), amount, Left(mid), _) => addCF.run((cfid, mid, amount, u.id))
//            case MealFoodDto(Right(a), amount, Right(b), date) => addFm.run((a, amount, b, u.id, date))
//            case MealFoodDto(Left(a), amount, Right(b), date)  => addCfm.run((a, amount, b, u.id, date))
//          } *> Ok()
    }
  }
}
