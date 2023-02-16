package user

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, ZIO, ZLayer}

trait UserDao {
  def create(username: String, email: String, password: String): IO[Throwable, Int]
}

object UserDao {
  def create(username: String, email: String, password: String): ZIO[UserDao, Throwable, Int] =
    ZIO.serviceWithZIO[UserDao](_.create(username, email, password))
}

case class UserDaoLive(source: Quill.Postgres[SnakeCase]) extends UserDao {
  import source._
  override def create(username: String, email: String, password: String): IO[Throwable, Int] = for {
    _    <- ZIO.debug(s"Creating user: $username")
    user <- User.from(0, username, email, password)
    id   <- run(querySchema[User]("users").insertValue(lift(user)).returningGenerated(generated => generated.userId))
  } yield id
}

object UserDaoLive {
  val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, UserDaoLive] = ZLayer.fromFunction(UserDaoLive(_))
}
