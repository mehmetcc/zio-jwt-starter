import configuration.{Configuration, ConfigurationLive}
import user.User
import zio._

import scala.language.postfixOps

object Main extends ZIOAppDefault {
  private lazy val program = for {
    _ <- ZIO.log("Starting now")
    _ <- ZIO.sleep(5 hours)
  } yield ()

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(ConfigurationLive.layer)
}
