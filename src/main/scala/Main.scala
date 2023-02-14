import configuration.{Configuration, ConfigurationLive}
import zio._

import scala.language.postfixOps

object Main extends ZIOAppDefault {
  private lazy val program = for {
    config <- Configuration.load
    _      <- ZIO.log(config toString)
  } yield ()

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(ConfigurationLive.layer)
}
