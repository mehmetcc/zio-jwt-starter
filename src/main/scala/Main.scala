import infrastructure.{ConfigurationLive, EncryptionLive, QuillContext}
import user.{UserApp, UserContext}
import zio._
import zio.http.Server

import scala.language.postfixOps

object Main extends ZIOAppDefault {
  private def startServer = Server.serve(UserApp())

  private lazy val program = for {
    _      <- ZIO.log("Application started")
    server <- startServer
  } yield ()

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      ConfigurationLive.layer,
      EncryptionLive.layer,
      UserContext.UserContext,
      Server.default
    )
}
