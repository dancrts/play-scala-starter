package controllers.api.onboarding

import io.scalaland.chimney.dsl.TransformationOps

import javax.inject.{Inject, Singleton}
import org.apache.pekko.http.scaladsl.common.StrictForm
import org.apache.pekko.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity}
import org.slf4j.LoggerFactory
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Files
import services.credentials.CredentialService

import services.onboarding.OnboardingService
import services.onboarding.dto.forms._
import services.onboarding.dto.requests._


@Singleton
class OnboardingImplementation @Inject()(
                                            onboardingService: OnboardingService,
                                            credentialService: CredentialService,
                                            val controllerComponents: ControllerComponents
                                        ) extends OnboardingController with BaseController {

    private val logger = LoggerFactory.getLogger(classOf[OnboardingImplementation])

    private val profileForm: Form[ProfileForm] = Form(
        mapping(
            "userKey" -> nonEmptyText,
            "firstName" -> nonEmptyText,
            "lastName" -> nonEmptyText,
            "profileURL" -> optional(text)
        )(ProfileForm.apply)(ProfileForm.unapply)
    )

    private val workspaceForm: Form[WorkspaceForm] = Form(
        mapping(
            "userKey" -> nonEmptyText,
            "name" -> nonEmptyText,
            "description" -> optional(text),
            "color" -> nonEmptyText
        )(WorkspaceForm.apply)(WorkspaceForm.unapply)
    )

    private def transformTemporaryFileToFileData(file: MultipartFormData.FilePart[Files.TemporaryFile]): StrictForm.FileData = {
        StrictForm.FileData(
            filename = Some(file.filename),
            entity = HttpEntity.Strict(
                contentType = getContentType(file.contentType),
                data = file.transformRefToBytes()
            )
        )
    }

    private def getContentType(fileContentType: Option[String]): ContentType = {
        ContentType.parse(fileContentType.getOrElse("application/octet-stream")) match {
            case Right(contentType) => contentType
            case Left(errors) =>
                logger.info("**** found errors while getting file ContentType: {}", errors)
                ContentTypes.`application/octet-stream`
        }
    }

    override def getUserMetadata(userKey: String): Action[AnyContent] = Action { implicit request =>
        credentialService.authenticateToken(request) match {
            case Some(decodedToken) =>
                println(decodedToken)
                Ok(s"Hello $userKey!")
            case None => Forbidden("no token >:(")
        }
        Ok(s"Hello $userKey!")
    }

    override def saveWorkspace: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
        val workspaceIcon = request.body.file("icon").map(transformTemporaryFileToFileData)
        workspaceForm.bindFromRequest().fold(
            errors => {
                logger.info("**** found errors while creating a workspace: {}", errors)
                BadRequest("Error")
            },
            workspace => {
                val workspaceToCreate: WorkspaceRequest = workspace.into[WorkspaceRequest].withFieldConst(_.icon, workspaceIcon).transform
                onboardingService.saveWorkspace(workspaceToCreate)
                println("workspace: {}", workspaceToCreate)
                Ok("Bien!")
            }
        )
    }

    override def saveProfile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
        val profileIcon = request.body.file("profileIcon").map(transformTemporaryFileToFileData)
        profileForm.bindFromRequest().fold(
            errors => {
                logger.info("**** found errors while creating a profile: {}", errors)
                BadRequest("Error, " + errors.toString)
            }, profile => {
                val profileToCreate: ProfileRequest = profile.into[ProfileRequest].withFieldConst(_.profileIcon, profileIcon).transform
                onboardingService.saveProfile(profileToCreate)
                println("profile: {}", profileToCreate)
                Ok("Bien")
            }
        )
    }
}
