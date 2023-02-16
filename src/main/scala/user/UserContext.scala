package user

import infrastructure.QuillContext
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.ZLayer

import javax.sql.DataSource

object UserContext {
  val UserContext
    : ZLayer[Any, Throwable, Quill.Postgres[SnakeCase.type] with DataSource with UserDao with UserService] =
    ZLayer.make[Quill.Postgres[SnakeCase.type] with DataSource with UserDao with UserService](
      UserDaoLive.layer,
      UserServiceLive.layer,
      QuillContext.QuillContext
    )
}
