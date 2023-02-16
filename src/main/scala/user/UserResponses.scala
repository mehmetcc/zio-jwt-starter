package user

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class RegisterResponse(userId: Int)

object RegisterResponse {
  implicit val encoder: JsonEncoder[RegisterResponse] = DeriveJsonEncoder.gen[RegisterResponse]
}

case class LoginResponse(token: String)

object LoginResponse {
  implicit val encoder: JsonEncoder[LoginResponse] = DeriveJsonEncoder.gen[LoginResponse]
}
