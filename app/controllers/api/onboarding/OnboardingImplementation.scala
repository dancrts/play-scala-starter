package controllers.api.onboarding

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Files
import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import controllers.api.CredentialService
import org.apache.pekko.http.scaladsl.common.StrictForm.FileData

import services.onboarding.dto.WorkspaceAndProfileRequest

@Singleton
class OnboardingImplementation @Inject()(credentialService: CredentialService,val controllerComponents: ControllerComponents)
    extends OnboardingController with BaseController {

    override def saveWorkspaceAndProfile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
        request.body.file("workspaceIcon") match {
            case Some(picture) =>
                // only get the last part of the filename
                // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
                val filename    = Paths.get(picture.filename).getFileName
                val fileSize    = picture.fileSize
                val contentType = picture.contentType
                val key         = picture.key
                picture.ref.copyTo(Paths.get(s"/tmp/picture/$filename"), replace = true)

        }

        Ok("File uploaded")
    }

    def upload: Action[AnyContent] = Action { request =>
        val algo = request.body.asMultipartFormData

        Ok("")
    }

    override def getUserMetadata(userKey: String): Action[AnyContent] = Action { request =>
        credentialService.authenticateToken(request) match {
            case Some(decodedToken) =>
                println(decodedToken)
                Ok(s"Hello $userKey!")
            case None => Forbidden("no token >:(")
        }
    }
}
