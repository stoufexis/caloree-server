package caloree.configuration

import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server

import cats.effect.{Async, Resource}
import cats.syntax.all._

import com.comcast.ip4s.{Host, Port}

case class ApiConfig(host: String, port: Int)

object ApiConfig {
  def server[F[_]: Async](apiConfig: ApiConfig)(app: HttpApp[F]): Resource[F, Server] = {
    val ApiConfig(h, p) = apiConfig
    val host            = Host.fromString(h).liftTo[F](new Exception(s"Couldn't parse $h to Host"))
    val port            = Port.fromInt(p).liftTo[F](new Exception(s"Couldn't parse $p to Port"))

    Resource.eval(host product port)
      .flatMap { case (host, port) =>
        EmberServerBuilder
          .default
          .withHost(host)
          .withPort(port)
          .withHttpApp(app)
          .build
      }
  }
}
