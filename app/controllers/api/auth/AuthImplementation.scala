package controllers.api.auth

import javax.inject._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

import models.{Account, ErrorCode}
import services.account.{AccountException, AccountService}
import services.account.dto._

@Singleton
class AuthImplementation @Inject()(accountService: AccountService ,val controllerComponents: ControllerComponents)
    extends AuthController with BaseController {

    implicit val formatError: OFormat[ErrorCode] = Json.format[ErrorCode]
    implicit val formatException: OFormat[AccountException] = Json.format[AccountException]
    implicit val formatAccount: OFormat[Account] = Json.format[Account]

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
                    case Left(exception) => BadRequest(Json.toJson(exception))
                    case Right(accId) => Ok(Json.obj("account" -> accId))
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
                    case Right(exception) => BadRequest(Json.toJson(exception))
                    case Left(acc) => Ok(Json.toJson(acc))
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
                accountService.googleLogin(data)
                Ok("Yikes!")
            }
        )
    }
}
