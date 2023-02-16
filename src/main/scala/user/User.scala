package user

import zio.{IO, Task, ZIO}

import scala.language.postfixOps

case class UserValidationException(message: String) extends Throwable(message)

case class User private (userId: Int, username: String, email: String, password: String)

object User {
  private def validateUsername(username: String): IO[String, String] =
    if (username.matches("^[a-zA-Z0-9]+")) ZIO.succeed(username)
    else ZIO.fail(s"$username should be alphanumeric")

  private def validateEmail(email: String): IO[String, String] = {
    // taken from https://stackoverflow.com/questions/13912597/validate-email-one-liner-in-scala
    val emailRegex =
      """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    def check(e: String): Boolean = e match {
      case null                                          => false
      case e if e.trim.isEmpty                           => false
      case e if emailRegex.findFirstMatchIn(e).isDefined => true
      case _                                             => false
    }

    if (check(email)) ZIO.succeed(email)
    else ZIO.fail(s"$email should be a valid email")
  }

  private def validatePassword(password: String): IO[String, String] =
    if (password.length >= 6) ZIO.succeed(password)
    else ZIO.fail("password should be at least 6 digits")

  def from(id: Int, username: String, email: String, password: String): Task[User] =
    ZIO
      .collectAllPar(List(validateUsername(username), validateEmail(email), validatePassword(password)))
      .withParallelism(3)
      .map(details => User(id, details.head, details(1), details(2)))
      .catchAll(errors => ZIO.fail(UserValidationException(errors)))
}
