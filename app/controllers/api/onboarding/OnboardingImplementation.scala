package controllers.api.onboarding

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Files
import org.apache.pekko.http.scaladsl.server.Directives._
import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import controllers.api.CredentialService
import org.apache.pekko.http.scaladsl.common.StrictForm.FileData

import services.onboarding.dto.WorkspaceAndProfileRequest

@Singleton
class OnboardingImplementation @Inject()(credentialService: CredentialService,val controllerComponents: ControllerComponents)
    extends OnboardingController with BaseController {

    override def saveWorkspaceAndProfile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
        val files = request.body.files

        files.foreach { file =>
            println(file)
        }
        Ok("File uploaded")
    }

    def upload: Action[AnyContent] = Action { request =>


        Ok
    }

    override def getUserMetadata(userKey: String): Action[AnyContent] = Action { request =>
//        credentialService.authenticateToken(request) match {
        ////            case Some(decodedToken) =>
        ////                println(decodedToken)
        ////                Ok(s"Hello $userKey!")
        ////            case None => Forbidden("no token >:(")
        ////        }
        Ok(s"Hello $userKey!")
    }
}
