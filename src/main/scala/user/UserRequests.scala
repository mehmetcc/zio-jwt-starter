package user

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class RegisterRequest(username: String, password: String, email: String)

object RegisterRequest {
  implicit val decoder: JsonDecoder[RegisterRequest] = DeriveJsonDecoder.gen[RegisterRequest]
}

case class LoginRequest(username: String, password: String)

object LoginRequest {
  implicit val decoder: JsonDecoder[LoginRequest] = DeriveJsonDecoder.gen[LoginRequest]
}
