package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder, Response}

import cats.syntax.all._
import cats.{Monad, MonadThrow}

import caloree.dto.CustomFoodDto
import caloree.model.Types.{CFID, Description, EntityId, Page, UID}
import caloree.model.{CustomFood, CustomFoodPreview, Nutrients, User}
import caloree.query.Run
import caloree.util._

import Params._

object CustomFoodRoutes {
  def routes[F[_]: MonadThrow: EntityDecoder[*[_], CustomFoodDto]](
      implicit
      getOne: Run.Optional[F, (CFID, UID), CustomFood],
      getMany: Run.Many[F, (Description, UID), CustomFoodPreview],
      add: Run.Unique[F, (UID, Description, Nutrients), Int],
      delete: Run.Unique[F, CFID, Int]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ / CFID(id) as u                                 => getOne.run((id, u.id)).asResponse
      case GET -> _ :? DescriptionP(d) +& PageP(p) +& Limit(l) as u => getMany.run((d, u.id), p, l).asResponse
      case DELETE -> _ / CFID(id) as _                              => delete.run(id) *> Ok()
      case req @ POST -> _ as u                                     =>
        req.decode.foldF(a => a.asResponse, cf => add.run((u.id, cf.description, cf.nutrients)) *> Ok())
    }
  }
}
