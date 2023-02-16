package infrastructure

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.config.ReadError
import zio.{IO, Task, ULayer, ZIO, ZLayer}

import java.security.MessageDigest
import java.time.Clock

trait Encryption {
  def sha256(text: String): Task[String]

  def jwtEncode(username: String): ZIO[Configuration, ReadError[String], String]

  def jwtDecode(token: String): ZIO[Configuration, ReadError[String], IO[Option[Nothing], JwtClaim]]
}

object Encryption {
  def sha256(text: String): ZIO[Encryption, Throwable, String] = ZIO.serviceWithZIO[Encryption](_.sha256(text))

  def jwtEncode(username: String): ZIO[Configuration with Encryption, ReadError[String], String] =
    ZIO.serviceWithZIO[Encryption](_.jwtEncode(username))

  def jwtDecode(token: String): ZIO[Configuration with Encryption, ReadError[String], IO[Option[Nothing], JwtClaim]] =
    ZIO.serviceWithZIO[Encryption](_.jwtDecode(token))
}

case class EncryptionLive() extends Encryption {
  implicit val clock: Clock = Clock.systemUTC // TODO find a way to work with ZIO-built-in clock instead of this?
  def sha256(password: String): Task[String] = ZIO.attempt(
    MessageDigest
      .getInstance("SHA-256")
      .digest(password.getBytes("UTF-8"))
      .map("%02x".format(_))
      .mkString
  )

  def jwtEncode(username: String): ZIO[Configuration, ReadError[String], String] = for {
    configuration <- Configuration.load
    json           = s"""{"user": "${username}"}"""
    claim          = JwtClaim(json).issuedNow.expiresIn(60)
    jwt            = Jwt.encode(claim, configuration.securityConfiguration.secretKey, JwtAlgorithm.HS512)
  } yield jwt

  def jwtDecode(token: String): ZIO[Configuration, ReadError[String], IO[Option[Nothing], JwtClaim]] = for {
    configuration <- Configuration.load
    decoded        = Jwt.decode(token, configuration.securityConfiguration.secretKey, Seq(JwtAlgorithm.HS512)).toOption
  } yield ZIO.fromOption(decoded)
}

object EncryptionLive {
  val layer: ULayer[EncryptionLive] = ZLayer.succeed(EncryptionLive())
}
