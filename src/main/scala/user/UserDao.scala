package user

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, ZIO, ZLayer}

import java.sql.SQLException

trait UserDao {
  def create(username: String, email: String, password: String): IO[Throwable, Int]

  def readById(id: Int): IO[SQLException, Option[User]]

  def readByUsername(username: String): IO[SQLException, Option[User]]

  def all: IO[SQLException, List[User]]
}

object UserDao {
  def create(username: String, email: String, password: String): ZIO[UserDao, Throwable, Int] =
    ZIO.serviceWithZIO[UserDao](_.create(username, email, password))

  def readById(id: Int): ZIO[UserDao, SQLException, Option[User]] = ZIO.serviceWithZIO[UserDao](_.readById(id))

  def readByUsername(username: String): ZIO[UserDao, SQLException, Option[User]] =
    ZIO.serviceWithZIO[UserDao](_.readByUsername(username))

  def all: ZIO[UserDao, SQLException, List[User]] = ZIO.serviceWithZIO[UserDao](_.all)
}

case class UserDaoLive(source: Quill.Postgres[SnakeCase]) extends UserDao {
  private final val TableName = "users"

  import source._

  override def create(username: String, email: String, password: String): IO[Throwable, Int] = for {
    user <- User.from(0, username, email, password)
    id   <- run(querySchema[User](TableName).insertValue(lift(user)).returningGenerated(generated => generated.userId))
  } yield id

  override def readById(id: Index): IO[SQLException, Option[User]] = run {
    querySchema[User](TableName).filter(current => current.userId == lift(id))
  }.map(result => result.headOption)

  override def readByUsername(username: String): IO[SQLException, Option[User]] = run {
    querySchema[User](TableName).filter(current => current.username == lift(username))
  }.map(result => result.headOption)

  override def all: IO[SQLException, List[User]] = run {
    querySchema[User](TableName)
  }
}

object UserDaoLive {
  val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, UserDaoLive] = ZLayer.fromFunction(UserDaoLive(_))
}
