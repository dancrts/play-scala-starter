package controllers.api.auth

import controllers.api.CredentialService
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.JsObject
import services.account.AccountService
import services.account.dto._

import javax.inject._

@Singleton
class AuthImplementation @Inject()(accountService: AccountService ,val controllerComponents: ControllerComponents)
    extends AuthController with BaseController {

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
        authForm.bindFromRequest.fold(
            errors => {
                errors.errors.foreach(println)
                BadRequest("Error!")
            },
            data => {
                accountService.register(data) match {
                    case Right(error) => BadRequest(error)
                    case Left(accId) => Ok(accId.toString)
                }

            }
        )


    }

    override def login: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

        authForm.bindFromRequest.fold(
            errors => {
                errors.errors.foreach(println)
                BadRequest("Error!")
            },
            data => {
                accountService.logIn(data) match {
                    case Right(error) => BadRequest(error)
                    case Left(accId) => Ok(accId.toString)
                }
            }
        )
    }

    override def loginWithGoogle: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        googleLoginForm.bindFromRequest.fold(
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
