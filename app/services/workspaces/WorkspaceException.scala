package services.workspaces

import models.ErrorCode
import services.workspaces.WorkspaceErrorCodes._

abstract class WorkspaceException(val errorCode: ErrorCode) {}

object WorkspaceExceptions {
    case class WorkspaceAlreadyExistsException(name: String) extends WorkspaceException(WorkspaceAlreadyExists(name))

    case class WorkspaceNotFoundException(workspaceKey: String) extends WorkspaceException(WorkspaceNotFound(workspaceKey))

    case class UserNotFoundException(userKey: String) extends WorkspaceException(ResourceNotFound())

    case class UnconfirmedUserException(userKey: String) extends WorkspaceException(UnconfirmedUser(userKey))

    case class UnconfirmedWorkspaceException(workspaceName: String) extends WorkspaceException(UnconfirmedWorkspace(workspaceName))

    case class UnknownException(error: String) extends WorkspaceException(UnknownError(error))

    case class UserHasNoPersonalWorkspaceException(error: String) extends WorkspaceException(UserHasNoPersonalWorkspace(error))
}


object WorkspaceErrorCodes {
    case class WorkspaceAlreadyExists(name: String) extends ErrorCode {
        override val code: String = "AU01"
        override val title: String = "Workspace already exists"
        override val detail: Option[String] = Some(name)
    }

    case class WorkspaceNotFound(name: String) extends ErrorCode {
        override val code: String = "AU06"
        override val title: String = "Workspace does not exists"
        override val detail: Option[String] = Some(name)
    }

    case class UnconfirmedUser(userKey: String) extends ErrorCode {
        override val code: String = "AU02"
        override val title: String = "User Unconfirmed or unaccepted"
        override val detail: Option[String] = Some(userKey)
    }

    case class UnconfirmedWorkspace(workspaceName: String) extends ErrorCode {
        override val code: String = "AU04"
        override val title: String = "WorkspaceRequest Unconfirmed or malformed"
        override val detail: Option[String] = Some(workspaceName)
    }

    case class UnknownError(error: String) extends ErrorCode {
        override val code: String = "AU03"
        override val title: String = "Unknown error"
        override val detail: Option[String] = Some(error)
    }

    case class UserHasNoPersonalWorkspace(error: String) extends ErrorCode {
        override val code: String = "AU05"
        override val title: String = "User Has no personal Workspace"
        override val detail: Option[String] = Some(error)
    }

    case class ResourceNotFound() extends ErrorCode {
        override val code = "ERR01"
        override val title = "Resource not found"
        override val detail: Option[String] = None
    }
}