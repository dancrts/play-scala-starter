package controllers.api.onboarding

import io.scalaland.chimney.dsl.{into, transformInto}
import models.{ErrorCode, Response}

import javax.inject.{Inject, Singleton}
import org.apache.pekko.http.scaladsl.common.StrictForm
import org.apache.pekko.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity}
import org.slf4j.LoggerFactory
import play.api.mvc.*
import play.api.data.Form
import play.api.data.Forms.*
import play.api.libs.Files
import play.api.libs.json.{Json, OFormat}
import services.account.AccountException
import services.credentials.CredentialService
import services.onboarding.OnboardingService
import services.onboarding.dto.forms.*
import services.onboarding.dto.requests.*
import services.onboarding.dto.responses.UserOnboardingResponse
import services.workspaces.WorkspaceException
import spray.json.JsValue


@Singleton
class OnboardingImplementation @Inject()(
                                            onboardingService: OnboardingService,
                                            credentialService: CredentialService,
                                            val controllerComponents: ControllerComponents
                                        ) extends OnboardingController with BaseController {
    
    implicit val formatError: OFormat[ErrorCode] = Json.format[ErrorCode]
    implicit val formatAccountException: OFormat[AccountException] = Json.format[AccountException]
    implicit val formatWorkspaceException: OFormat[WorkspaceException] = Json.format[WorkspaceException]
    implicit val formatUserOnboardingResponse: OFormat[UserOnboardingResponse] = Json.format[UserOnboardingResponse]
    implicit val formatResponse: OFormat[Response] = Json.format[Response]
    
    
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



    override def getUserMetadata(userKey: String): Action[AnyContent] = Action { implicit request =>
        credentialService.authenticateToken(request) match {
            case Some(decodedToken) =>
                onboardingService.getUserMetadata(userKey) match
                    case Left(exception) => accountExceptionToResult(exception)
                    case Right(userResponse) => Ok(Json.toJson(userResponse))
            case None => Unauthorized(Json.obj("unauthorized" -> "Please Log In Again"))
        }
    }

    override def saveWorkspace: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
        credentialService.authenticateToken(request) match {
            case Some(decodedToken) =>
                println(decodedToken)
                val workspaceIcon = request.body.file("icon").map(transformTemporaryFileToFileData)
                workspaceForm.bindFromRequest().fold(
                    errors => {
                        logger.info("**** found errors while creating a workspace: {}", errors)
                        BadRequest(Json.obj("error" -> "Something went wrong, try again later"))
                    },
                    workspace => {
                        val workspaceToCreate: WorkspaceRequest = workspace.into[WorkspaceRequest].withFieldConst(_.icon, workspaceIcon).transform
                        onboardingService.saveWorkspace(workspaceToCreate) match
                            case Left(exception) => workspaceExceptionToResult(exception)
                            case Right(response) => Ok(Json.toJson(response))
                    }
                )
            case None => Unauthorized(Json.obj("unauthorized" -> "Please Log In Again"))
        }
    }

    override def saveProfile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
        credentialService.authenticateToken(request) match {
            case Some(decodedToken) =>
                val profileIcon = request.body.file("profileIcon").map(transformTemporaryFileToFileData)
                profileForm.bindFromRequest().fold(
                    errors => {
                        logger.info("**** found errors while creating a profile: {}", errors)
                        BadRequest(Json.obj("error" -> "Something went wrong, try again later"))
                    }, profile => {
                        val profileToCreate: ProfileRequest = profile.into[ProfileRequest].withFieldConst(_.profileIcon, profileIcon).transform
                        onboardingService.saveProfile(profileToCreate) match
                            case Left(exception) => accountExceptionToResult(exception)
                            case Right(response) => Ok(Json.toJson(response))
                    }
                )
            case None => Unauthorized(Json.obj("unauthorized" -> "Please Log In Again"))
        }
        
    }

    private def accountExceptionToResult(exception: AccountException): Result = {
        exception match {
            case e: AccountException.AccountNotFoundException => NotFound(Json.toJson(exception))
            case e: AccountException.AppTackConfigurationException => BadRequest(Json.toJson(exception))
            case e: AccountException.InvalidCredentialsException => Unauthorized(Json.toJson(exception))
            case e: AccountException.MalformedAppleTokenException => BadRequest(Json.toJson(exception))
            case e: AccountException.MalformedGoogleTokenException => BadRequest(Json.toJson(exception))
            case e: AccountException.ResourceNotFoundException => NotFound(Json.toJson(exception))
            case e: AccountException.UnknownException => InternalServerError(Json.toJson(exception))
            case e: AccountException.UserAlreadyExistsException => Conflict(Json.toJson(exception))
            case e: AccountException.UserNotFoundException => NotFound(Json.toJson(exception))
            case _ => InternalServerError(Json.toJson(exception))
        }
    }
    
    private def workspaceExceptionToResult(exception: WorkspaceException): Result = {
        exception match
            case e: WorkspaceException.AccountDoesNotExistsException => NotFound(Json.toJson(exception))
            case e: WorkspaceException.WorkspaceAlreadyExistsException => Conflict(Json.toJson(exception))
            case e: WorkspaceException.WorkspaceNotFoundException => NotFound(Json.toJson(exception))
            case e: WorkspaceException.UserNotFoundException => NotFound(Json.toJson(exception))
            case e: WorkspaceException.UnconfirmedUserException => BadRequest(Json.toJson(exception))
            case e: WorkspaceException.UnconfirmedWorkspaceException => BadRequest(Json.toJson(exception))
            case e: WorkspaceException.UnknownException => InternalServerError(Json.toJson(exception))
            case e: WorkspaceException.UserHasNoPersonalWorkspaceException => Conflict(Json.toJson(exception))
            case _ => InternalServerError(Json.toJson(exception))
    }

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
}
