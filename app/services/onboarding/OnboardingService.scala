package services.onboarding

import com.qrsof.apptack.client.ApptackClient
import io.scalaland.chimney.dsl.{into, transformInto}

import javax.inject.{Inject, Singleton}
import java.util.{Date, UUID}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try
import play.api.libs.json.{JsObject, JsValue, Json}
import dao.{AccountDAO, WorkspaceDAO}
import models.{Account, Response, Workspace}
import services.account.AccountException
import services.files.FileManagementService
import services.files.dto.FileKey
import services.onboarding.dto.requests.*
import services.onboarding.dto.responses.UserOnboardingResponse
import services.workspaces.WorkspaceException

//@Singleton
class OnboardingService @Inject()(
                                     workspaceDAO: WorkspaceDAO,
                                     accountDAO: AccountDAO,
                                     fileManagementService: FileManagementService,
                                     appTackClient: ApptackClient
                                 )(implicit ex: ExecutionContext) {

    def getUserMetadata(userKey: String): Either[AccountException, UserOnboardingResponse] = {
        appTackClient.users.findUserByKey(userKey) match {
            case Left(exception) => Left(AccountException.UserNotFoundException(userKey))
            case Right(user) =>
                user.metadata match {
                    case Some(metadata) =>
                        Right(
                            UserOnboardingResponse(
                                name = getJsObjectValue(metadata, "givenName"),
                                lastname = getJsObjectValue(metadata, "familyName"),
                                pictureURL = getJsObjectValue(metadata, "pictureUrl")
                            )
                        )
                    case None => Left(AccountException.UnknownException(userKey))
                }
        }
    }

    def saveWorkspace(workspaceRequest: WorkspaceRequest): Either[WorkspaceException, Response] = {
        appTackClient.users.accounts.get(workspaceRequest.userKey) match {
            case Left(value) => Left(WorkspaceException.AccountDoesNotExistsException(workspaceRequest.userKey))
            case Right(accounts) =>
                val workspaceKey = accounts.head.accountKey
                val maybeWorkspaceIconKey: Option[FileKey] = Await.result(saveWorkspaceIcon(workspaceRequest), Duration.Inf)

                val newWorkspace: Workspace = workspaceRequest.into[Workspace]
                    .withFieldConst(_.imageKey, optionStrToUUID(maybeWorkspaceIconKey.map(_.key)))
                    .withFieldConst(_.key, UUID.fromString(workspaceKey))
                    .withFieldConst(_.createdAt, new Date())
                    .withFieldConst(_.modifiedAt, new Date())
                    .transform
                val createdWs = workspaceDAO.createWorkspace(newWorkspace)
                Right(Response(createdWs.key.toString, Some("Workspace created successfully")))
        }
    }

    def saveProfile(profileRequest: ProfileRequest): Either[AccountException, Response] = {
        val fullName = profileRequest.firstName + " " + profileRequest.lastName
        val maybeProfileIconKey: Option[String] = Await.result(saveProfileIcon(profileRequest), Duration.Inf)
        val metadata = formatMetadata(fullName, maybeProfileIconKey)
        appTackClient.users.updateUserMetadata(profileRequest.userKey, metadata) match {
            case Left(exception) => Left(AccountException.UserNotFoundException(profileRequest.userKey))
            case Right(userKey) =>
                accountDAO.findByKey(UUID.fromString(userKey.key)) match {
                    case Some(account) =>
                        val updatedAccount = account.copy(fullname = fullName, profilePic = optionStrToUUID(maybeProfileIconKey))
                        val updatedResult = Await.result(accountDAO.updateAccount(updatedAccount), Duration.Inf)
                        updatedResult match {
                            case 1 => Right(Response(account.accountKey.toString, Some("User profile updated successfully")))
                            case _ => Left(AccountException.UnknownException(profileRequest.userKey))
                        }
                    case None =>
                        Left(AccountException.UserNotFoundException(profileRequest.userKey))
                }
                
        }
        
        
    }


    private def saveProfileIcon(profileRequest: ProfileRequest): Future[Option[String]] = {
        profileRequest.profileIcon match {
            case Some(profileIcon) => fileManagementService.save(profileRequest.userKey, profileIcon).map(_.map(_.key))
            case None => Future {
                profileRequest.profileURL match {
                    case Some(value) => Some(value)
                    case None => None
                }
            }
        }
    }

    private def saveWorkspaceIcon(workspaceRequest: WorkspaceRequest): Future[Option[FileKey]] = {
        workspaceRequest.icon match {
            case Some(workspaceIcon) => fileManagementService.save(workspaceRequest.userKey, workspaceIcon)
            case None => Future(None)
        }
    }

    private def formatMetadata(fullName: String, icon: Option[String]): JsObject = {
        Json.obj("" -> fullName, "" -> icon)
    }

    private def getJsObjectValue(jsonObject: JsObject, field: String): String = {
        val jsValue = jsonObject.value(field).toString
        println(jsValue)
        ???
    }


    private def optionStrToUUID(optional: Option[String]): Option[UUID] =
        optional.flatMap { s => Try(UUID.fromString(s)).toOption }

    private def optionUuidToStr(optional: Option[UUID]): Option[String] =
        optional.flatMap { s => Try(s.toString).toOption }
}
