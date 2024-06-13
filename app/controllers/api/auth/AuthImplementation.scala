package controllers.api.auth

import javax.inject._
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
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
                    case Left(exception) => BadRequest("")
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
                    case Right(exception) => BadRequest("")
                    case Left(acc) => Ok(Json.toJson(acc))
                }
            }
        )
    }

    override def loginWithGoogle: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        googleLoginForm.bindFromRequest().fold(
            errors => {
                BadRequest("Error!")
            },
            data => {
                accountService.googleLogin(data)
                Ok("Yikes!")
            }
        )
    }
}
