package services.account.dto

case class AuthRequest(email: String, password: String)

object AuthRequest {
    def unapply(authReq: AuthRequest): Option[(String, String)] = Some((authReq.email, authReq.password))
}
