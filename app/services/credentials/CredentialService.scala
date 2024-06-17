package services.credentials

import com.qrsof.jwt.models.{DecodedToken, JwtToken}
import com.qrsof.jwt.validation.JwtValidationService
import play.api.mvc.RequestHeader

import javax.inject.{Inject, Singleton}

@Singleton
class CredentialService @Inject()(jwtValidationService: JwtValidationService) {

    def authenticateToken(req: RequestHeader): Option[DecodedToken] = {
        val authorization: Option[String] = req.headers.get("Authorization")
        authorization match {
            case Some(token) =>
                jwtValidationService.validateJwt(JwtToken(token)) match {
                    case Left(value) => None
                    case Right(decodedToken) => Some(decodedToken)
                }
            case _ => None
        }
    }
}
