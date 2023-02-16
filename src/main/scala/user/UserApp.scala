package user

import infrastructure.{Configuration, Encryption}
import zio.ZIO
import zio.http._
import zio.http.model.{HttpError, Method}
import zio.json._

import java.nio.charset.StandardCharsets

object UserApp {
  def apply(): Http[Configuration with Encryption with UserService, Nothing, Any, Response] =
    Http.collectZIO {
      case req @ Method.POST -> !! / "user" / "signup" =>
        register(req.asInstanceOf[Request]).fold(
          success = value => Response.json(value.toJson),
          failure =
            error => Response.fromHttpError(HttpError.InternalServerError(error.asInstanceOf[Throwable].getMessage))
        )
      case req @ Method.POST -> !! / "user" / "signin" =>
        login(req.asInstanceOf[Request]).fold(
          success = value => Response.json(value.toJson),
          failure = {
            case error @ UserNotFoundException =>
              Response.fromHttpError(HttpError.NotFound(error.asInstanceOf[Throwable].getMessage))
            case error @ FaultyPasswordException =>
              Response.fromHttpError(HttpError.Unauthorized(error.asInstanceOf[Throwable].getMessage))
            case error =>
              Response.fromHttpError(HttpError.InternalServerError(error.asInstanceOf[Throwable].getMessage))
          }
        )
    }

  private def register(request: Request): ZIO[Encryption with UserService, Serializable, RegisterResponse] = for {
    json    <- request.body.asString(StandardCharsets.UTF_8)
    either   = json.fromJson[RegisterRequest]
    decoded <- ZIO.fromEither(either)
    userId  <- UserService.createUser(decoded.username, decoded.email, decoded.password)
  } yield RegisterResponse(userId)

  private def login(
    request: Request
  ): ZIO[Configuration with Encryption with UserService, Serializable, LoginResponse] =
    for {
      json    <- request.body.asString(StandardCharsets.UTF_8)
      either   = json.fromJson[LoginRequest]
      decoded <- ZIO.fromEither(either)
      found   <- UserService.loginUser(decoded.username, decoded.password)
    } yield LoginResponse(found)
}
