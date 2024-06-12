package controllers.api.onboarding

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.{Inject, Singleton}

import controllers.api.CredentialService

@Singleton
class OnboardingImplementation @Inject()(credentialService: CredentialService,val controllerComponents: ControllerComponents)
    extends OnboardingController with BaseController {

    override def saveWorkspaceAndProfile: Action[AnyContent] = ???

    override def getUserMetadata(userKey: String): Action[AnyContent] = Action { request =>
        credentialService.authenticateToken(request) match {
            case Some(decodedToken) => Ok(s"Hello $userKey!")
            case None => Forbidden
        }
    }
}
