package services.onboarding

import com.qrsof.apptack.client.ApptackClient
import io.scalaland.chimney.dsl.{into, transformInto}

import javax.inject.{Inject, Singleton}
import java.util.UUID
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try
import play.api.libs.json.{JsValue, JsObject}
import dao.{AccountDAO, WorkspaceDAO}
import models.Workspace
import services.files.FileManagementService
import services.files.dto.FileKey
import services.onboarding.dto.requests.*
import services.onboarding.dto.responses.UserOnboardingResponse

//@Singleton
class OnboardingService @Inject()(
                                     workspaceDAO: WorkspaceDAO,
                                     accountDAO: AccountDAO,
                                     fileManagementService: FileManagementService,
                                     appTackClient: ApptackClient
                                 )(implicit ex: ExecutionContext) {

    def getUserMetadata(userKey: String): Either[String, UserOnboardingResponse] = {
        appTackClient.users.findUserByKey(userKey) match {
            case Left(exception) => Left("error")
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
                    case None => Left("error")
                }
        }
//        ???
    }

    def saveWorkspace(workspaceRequest: WorkspaceRequest): Either[String, Nothing] = {
        appTackClient.users.accounts.get(workspaceRequest.userKey) match {
            case Left(value) => Left("Error")
            case Right(accounts) =>
                val workspaceKey = accounts.head.accountKey
                val maybeWorkspaceIconKey: Option[FileKey] = Await.result(saveWorkspaceIcon(workspaceRequest), Duration.Inf)

                val newWorkspace: Workspace = workspaceRequest.into[Workspace]
                    .withFieldConst(_.imageKey, optionStrToUUID(maybeWorkspaceIconKey.map(_.key)))
                    .withFieldConst(_.key, UUID.fromString(workspaceKey)).transform

                Right(workspaceDAO.createWorkspace(newWorkspace))
        }
    }

    def saveProfile(profileRequest: ProfileRequest) = {
        val fullName = profileRequest.firstName + " " + profileRequest.lastName
        val maybeProfileIconKey: Option[String] = Await.result(saveProfileIcon(profileRequest), Duration.Inf)
        val metadata = formatMetadata(fullName, maybeProfileIconKey)
        appTackClient.users.updateUserMetadata(profileRequest.userKey, metadata) match
            case Left(exception) =>
                println(exception)
            case Right(value) =>
                println(value)
//        appTackClient.users.updateUserMetadataTupled(profileRequest.userKey, metadata: _ *) match {
//            case Left(exception) =>
//                throw new RuntimeException("Something went wrong: " + exception.toString)
//            case Right(value) =>
//                println(value)
//        }
        accountDAO.findByKey(UUID.fromString(profileRequest.userKey)) match {
            case Some(account) =>
                val updatedAccount = account.copy(fullname = fullName, profilePic = optionStrToUUID(maybeProfileIconKey))
                val updatedResult = Await.result(accountDAO.updateAccount(updatedAccount), Duration.Inf)
                updatedResult match {
                    case 1 => println("it worked!")
                    case _ => println("it didn't work")
                }
            case None =>
                //this should return an exception
                println("None")
        }
        ???
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
        ???
    }

    private def getJsObjectValue(jsonObject: JsObject, field: String): String = {
        val jsValue = jsonObject.value(field)
        jsValue.toString
        ???
    }


    private def optionStrToUUID(optional: Option[String]): Option[UUID] =
        optional.flatMap { s => Try(UUID.fromString(s)).toOption }

    private def optionUuidToStr(optional: Option[UUID]): Option[String] =
        optional.flatMap { s => Try(s.toString).toOption }
}
