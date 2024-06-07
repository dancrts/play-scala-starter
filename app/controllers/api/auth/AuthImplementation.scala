package controllers.api.auth

import io.scalaland.chimney.dsl.TransformerOps
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import services.account.AccountService
import services.account.dto._

import javax.inject._

@Singleton
class AuthImplementation @Inject()(accountService: AccountService ,val controllerComponents: ControllerComponents) (
    implicit system: ActorSystem, mat: Materializer
) extends AuthController with BaseController {

    private val authForm: Form[AuthRequest] = Form(
        mapping("email" -> nonEmptyText, "password" -> nonEmptyText)(AuthRequest.apply)(AuthRequest.unapply)
    )

    override def register: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        authForm.bindFromRequest.fold(
            errors => {
                errors.errors.foreach(println)
                BadRequest("Error!")
            },
            data => {
                val registerReq = AuthRequest(data.email, data.password)
                val accountID = accountService.register(registerReq)
                Ok(accountID.toString)
            }
        )


    }

    override def login: Action[AnyContent] = ???
}
