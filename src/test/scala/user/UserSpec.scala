package user

import zio.Scope
import zio.test.Assertion.{equalTo, fails, succeeds}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertZIO}

object UserSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("User Suite")(
    test("valid input given should validate and create a User object") {
      assertZIO(User.from(username = "username", password = "password", email = "email@email.com").exit) {
        succeeds(equalTo(User(username = "username", password = "password", email = "email@email.com")))
      }
    },
    test("invalid password given should raise an error") {
      assertZIO(User.from(username = "username", password = "short", email = "email@email.com").exit) {
        fails(equalTo(UserValidationException("password should be at least 6 digits")))
      }
    },
    test("invalid email given should raise an error") {
      // honestly, can't be bothered with testing everything that regex offers
      assertZIO(User.from(username = "username", password = "password", email = "not_an_email").exit) {
        fails(equalTo(UserValidationException("not_an_email should be a valid email")))
      }
    },
    test("invalid username given should raise an error") {
      assertZIO(User.from(username = "?", password = "password", email = "email@email.com").exit) {
        fails(equalTo(UserValidationException("? should be alphanumeric")))
      }
    }
  )
}
