package caloree.routes

import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder}

import cats.MonadThrow
import cats.syntax.all._

import caloree.dto.CustomFoodDto
import caloree.model.Types.{CFID, Description, UID}
import caloree.model.{CustomFood, CustomFoodPreview, Nutrients, User}
import caloree.query.Run
import caloree.routes.Params._
import caloree.util._

object CustomFoodRoutes {
  def routes[F[_]: MonadThrow: EntityDecoder[*[_], CustomFoodDto]](
      implicit
      getOne: Run.Optional[F, (CFID, UID), CustomFood],
      getMany: Run.Many[F, (Description, UID), CustomFoodPreview],
      add: Run.Update[F, (UID, Description, Nutrients)],
      delete: Run.Update[F, CFID]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ / CFID(id) as u                                  => getOne.run((id, u.id)).asResponse
      case GET -> _ :? DescriptionP(d) +& PageP(p) +& LimitP(l) as u => getMany.run((d, u.id), p, l).asResponse
      case DELETE -> _ / CFID(id) as _                               => delete.run(id) *> Ok()
      case req @ POST -> _ as u                                      =>
        req.decode.foldF(_.asResponse, cf => add.run((u.id, cf.description, cf.nutrients)) *> Ok())
    }
  }
}
