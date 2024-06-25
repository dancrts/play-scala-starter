package controllers.api.auth

import javax.inject._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import com.qrsof.jwt.models.JwtToken
import models.{Account, ErrorCode}
import services.account.AccountException._
import services.account.{AccountException, AccountService}
import services.account.dto._


@Singleton
class AuthImplementation @Inject()(accountService: AccountService ,val controllerComponents: ControllerComponents)
    extends AuthController with BaseController {

    implicit val formatError: OFormat[ErrorCode] = Json.format[ErrorCode]
    implicit val formatException: OFormat[AccountException] = Json.format[AccountException]
    implicit val formatAccount: OFormat[Account] = Json.format[Account]
    implicit val formatJwt: OFormat[JwtToken] = Json.format[JwtToken]

    private val authForm: Form[AuthRequest] = Form(
        mapping(
            "email" -> nonEmptyText,
            "password" -> nonEmptyText
        )(AuthRequest.apply)(AuthRequest.unapply)
    )

    private val googleLoginForm: Form[GoogleLoginRequest] = Form(
        mapping(
            "idToken" -> nonEmptyText,
            "deviceInformation" -> mapping(
                "platform" -> nonEmptyText,
                "deviceId"-> optional(text),
                "deviceDescription"->optional(text)
            )(DeviceInformation.apply)(DeviceInformation.unapply),
            "userInformation" -> mapping(
                "email" -> nonEmptyText,
                "name" -> nonEmptyText,
                "photoUrl" -> nonEmptyText
            )(UserInformation.apply)(UserInformation.unapply)
        )(GoogleLoginRequest.apply)(GoogleLoginRequest.unapply)
    )

    override def register: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        authForm.bindFromRequest().fold(
            errors => {
                errors.errors.foreach(println)
                BadRequest("Error!")
            },
            data => {
                accountService.register(data) match {
                    case Left(exception) => exceptionToResult(exception)
                    case Right(jwt) => Ok(Json.toJson(jwt))
                }
            }
        )
    }

    override def login: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        authForm.bindFromRequest().fold(
            errors => {
                errors.errors.foreach(println)
                BadRequest("Error!")
            },
            data => {
                accountService.logIn(data) match {
                    case Right(jwt) => Ok(Json.toJson(jwt))
                    case Left(exception) => exceptionToResult(exception)
                }
            }
        )
    }

    override def loginWithGoogle: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        googleLoginForm.bindFromRequest().fold(
            errors => {
                println(errors.errors.toString())
                BadRequest("Error!")
            },
            data => {
                accountService.googleLogin(data) match {
                    case Left(exception) => exceptionToResult(exception)
                    case Right(token) => Ok(Json.toJson(token))
                }
            }
        )
    }


    private def exceptionToResult(exception: AccountException): Result = {
        exception match {
            case e: AccountNotFoundException => NotFound(Json.toJson(exception))
            case e: AppTackConfigurationException => BadRequest(Json.toJson(exception))
            case e: InvalidCredentialsException => Unauthorized(Json.toJson(exception))
            case e: MalformedAppleTokenException => BadRequest(Json.toJson(exception))
            case e: MalformedGoogleTokenException => BadRequest(Json.toJson(exception))
            case e: ResourceNotFoundException => NotFound(Json.toJson(exception))
            case e: UnknownException => InternalServerError(Json.toJson(exception))
            case e: UserAlreadyExistsException => Conflict(Json.toJson(exception))
            case e: UserNotFoundException => NotFound(Json.toJson(exception))
            case _ => InternalServerError(Json.toJson(exception))
        }
    }
}
