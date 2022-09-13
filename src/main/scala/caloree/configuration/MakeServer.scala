package caloree.configuration

import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server

import cats.effect.{Async, Resource}

import com.comcast.ip4s.{Host, IpLiteralSyntax, Port}

object MakeServer {
  def apply[F[_]: Async](app: HttpApp[F]): Resource[F, Server] =
    EmberServerBuilder
      .default
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(app)
      .build
}
