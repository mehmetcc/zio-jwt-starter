package user

import infrastructure.{Configuration, Encryption}
import zio.{ZIO, ZLayer}

import java.sql.SQLException

case object UserNotFoundException extends Throwable("User with given username cannot be found!")

case object FaultyPasswordException extends Throwable("Given password is not right!")

trait UserService {
  def createUser(username: String, email: String, password: String): ZIO[Encryption, Throwable, Int]

  def loginUser(username: String, password: String): ZIO[Configuration with Encryption, Throwable, String]
}
object UserService {
  def createUser(username: String, email: String, password: String): ZIO[Encryption with UserService, Throwable, Int] =
    ZIO.serviceWithZIO[UserService](_.createUser(username, email, password))

  def loginUser(
    username: String,
    password: String
  ): ZIO[Configuration with Encryption with UserService, Throwable, String] =
    ZIO.serviceWithZIO[UserService](_.loginUser(username, password))
}

case class UserServiceLive(dao: UserDao) extends UserService {
  override def createUser(username: String, email: String, password: String): ZIO[Encryption, Throwable, Int] = for {
    encrypted <- Encryption.sha256(password)
    id        <- dao.create(username, email, encrypted)
  } yield id

  override def loginUser(
    username: String,
    password: String
  ): ZIO[Configuration with Encryption, Throwable, String] = for {
    found     <- dao.readByUsername(username)
    user      <- ZIO.fromOption(found).orElseFail(UserNotFoundException)
    encrypted <- Encryption.sha256(password)
    token     <- Encryption.jwtEncode(username)
    result    <- if (encrypted == user.password) ZIO.succeed(token) else ZIO.fail(FaultyPasswordException)
  } yield result
}

object UserServiceLive {
  val layer: ZLayer[UserDao, Nothing, UserServiceLive] = ZLayer.fromFunction(UserServiceLive(_))
}
