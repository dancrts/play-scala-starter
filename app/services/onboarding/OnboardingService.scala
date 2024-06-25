package services.onboarding

import com.qrsof.apptack.client.ApptackClient
import io.scalaland.chimney.dsl.TransformationOps

import javax.inject.{Inject, Singleton}
import java.util.UUID
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try
import spray.json.JsValue
import dao.{AccountDAO, WorkspaceDAO}
import models.Workspace
import services.files.FileManagementService
import services.files.dto.FileKey
import services.onboarding.dto.requests._
import services.onboarding.dto.responses.UserOnboardingResponse

//@Singleton
class OnboardingService @Inject()(
                                     workspaceDAO: WorkspaceDAO,
                                     accountDAO: AccountDAO,
                                     fileManagementService: FileManagementService,
                                     appTackClient: ApptackClient
                                 )(implicit ex: ExecutionContext) {

    def getUserMetadata(userKey: String): Either[UserOnboardingResponse, String] = {
//        appTackClient.users.findUserByKey(userKey) match {
//            case Left(exception) => Left("error")
//            case Right(user) =>
//                user.metadata match {
//                    case Some(metadata) =>
//                        Right(
//                            UserOnboardingResponse(
//                                name = getJsObjectValue(metadata, "givenName"),
//                                lastname = getJsObjectValue(metadata, "familyName"),
//                                pictureURL = getJsObjectValue(metadata, "pictureUrl")
//                            )
//                        )
//                    case None => Left("error")
//                }
//        }
        ???
    }

    def saveWorkspace(workspaceRequest: WorkspaceRequest) = {
//        val accounts = appTackClient.users.accounts.get(workspaceRequest.userKey).toSeq.head
//        val workspaceKey = accounts.head.accountKey
//
//        val maybeWorkspaceIconKey: Option[FileKey] = Await.result(saveWorkspaceIcon(workspaceRequest), Duration.Inf)
//
//        val newWorkspace: Workspace = workspaceRequest.into[Workspace]
//            .withFieldConst(_.imageKey, optionStrToUUID(maybeWorkspaceIconKey.map(_.key)))
//            .withFieldConst(_.key, workspaceKey)
//            .transform
//        workspaceDAO.createWorkspace(newWorkspace)


        ???
    }

    def saveProfile(profileRequest: ProfileRequest) = {
//        val fullName = profileRequest.firstName + " " + profileRequest.lastName
//        val maybeProfileIconKey: Option[String] = Await.result(saveProfileIcon(profileRequest), Duration.Inf)
//        val metadata = formatMetadata(fullName, maybeProfileIconKey)
//        appTackClient.users.updateUserMetadataTupled(profileRequest.userKey, metadata: _ *) match {
//            case Left(exception) =>
//                throw new RuntimeException("Something went wrong: " + exception.toString)
//            case Right(value) =>
//                println(value)
//        }
//        accountDAO.findByKey(UUID.fromString(profileRequest.userKey)) match {
//            case Some(account) =>
//                val updatedAccount = account.copy(fullname = fullName, profilePic = optionStrToUUID(maybeProfileIconKey))
//                val updatedResult = Await.result(accountDAO.updateAccount(updatedAccount), Duration.Inf)
//                updatedResult match {
//                    case 1 => println("it worked!")
//                    case _ => println("it didn't work")
//                }
//            case None =>
//                //this should return an exception
//                println("None")
//        }
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

    private def formatMetadata(fullName: String, icon: Option[String]): Seq[(String, Object)] = {
        Seq("fullName" -> fullName) ++ icon.fold[Seq[(String, Object)]](Seq.empty)(key => Seq("picture" -> key))
    }

    private def getJsObjectValue(value: JsValue, field: String): String = {
        value.asJsObject().getFields(field).headOption.fold("")(_.toString().dropRight(1).drop(1))
    }

    private def optionStrToUUID(optional: Option[String]): Option[UUID] = {
        optional.flatMap { s =>
            Try(UUID.fromString(s)).toOption
        }
    }

    private def optionUuidToStr(optional: Option[UUID]): Option[String] = {
        optional.flatMap { s =>
            Try(s.toString).toOption
        }
    }
}
