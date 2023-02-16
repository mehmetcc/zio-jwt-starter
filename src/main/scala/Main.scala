import infrastructure.{Configuration, ConfigurationLive, QuillContext}
import user.{UserDao, UserDaoLive}
import zio._

import scala.language.postfixOps

object Main extends ZIOAppDefault {
  private lazy val program = for {
    _             <- ZIO.log("Starting now")
    configuration <- Configuration.load
    created       <- UserDao.create("anan", "anan@anan.com", "123456")
    _             <- ZIO.log(created toString)
  } yield ()

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      ConfigurationLive.layer,
      UserDaoLive.layer,
      QuillContext.QuillContext
    )
}
