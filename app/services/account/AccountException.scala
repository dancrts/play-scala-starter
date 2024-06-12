package services.account

import com.qrsof.exceptions.{ApplicationException, ErrorCode}
import services.account.AccountErrorCodes._


sealed abstract class AccountException(errorCode: ErrorCode) extends ApplicationException(errorCode)

object AccountException {
    case class AccountNotFoundException(resource: String) extends AccountException(AccountNotFound(resource))

    case class AppNotFoundException(appKey: String) extends AccountException(AppNotFound(appKey))

    case class AppTackConfigurationException(resource: String) extends AccountException(AppTackConfigurationError(resource))

    case class InvalidCredentialsException(username: String) extends AccountException(InvalidCredentials(username))

    case class MalformedAppleTokenException(userResource: String) extends AccountException(MalformedAppleToken(userResource))

    case class MalformedGoogleTokenException(userResource: String) extends AccountException(MalformedGoogleToken(userResource))

    case class ResourceNotFoundException(resource: String) extends AccountException(ResourceNotFound(resource))

    case class UnknownException(username: String) extends AccountException(UnknownError(username))

    case class UserAlreadyExistsException(username: String) extends AccountException(UserAlreadyExists(username))

    case class UserNotFoundException(username: String) extends AccountException(UserNotFound(username))
}

object AccountErrorCodes {

    case class AccountNotFound(resource: String) extends ErrorCode {
        override val code: String = "OAC07"
        override val title: String = "Account not found"
        override val detail: Option[String] = Some(resource)
    }

    case class AppNotFound(appKey: String) extends ErrorCode {
        override val code: String = "AME01"
        override val title: String = "App not found"
        override val detail: Option[String] = Some(appKey)
    }

    case class AppTackConfigurationError(configErrorDetail: String) extends ErrorCode {
        override val code: String = "AME07"
        override val title: String = "Apptack configuration error"
        override val detail: Option[String] = Some(configErrorDetail)
    }

    case class InvalidCredentials(username: String) extends ErrorCode {
        override val code: String = "OAC02"
        override val title: String = "Credential invalid"
        override val detail: Option[String] = Some(username)
    }

    case class MalformedAppleToken(userResource: String) extends ErrorCode {
        override val code: String = "OAC06"
        override val title: String = "Malformed Apple sso token"
        override val detail: Option[String] = Some(userResource)
    }

    case class MalformedGoogleToken(userResource: String) extends ErrorCode {
        override val code: String = "OAC06"
        override val title: String = "Malformed google sso token"
        override val detail: Option[String] = Some(userResource)
    }

    case class ResourceNotFound(resource: String) extends ErrorCode {
        override val code: String = "OAC07"
        override val title: String = "Resource not found"
        override val detail: Option[String] = Some(resource)
    }

    case class UnknownError(username: String) extends ErrorCode {
        override val code: String = "OAC05"
        override val title: String = "Unexpected error"
        override val detail: Option[String] = Some(username)
    }

    case class UserAlreadyExists(username: String) extends ErrorCode {
        override val code: String = "OAC03"
        override val title: String = "User already exists"
        override val detail: Option[String] = Some(username)
    }

    case class UserNotFound(username: String) extends ErrorCode {
        override val code: String = "OAC01"
        override val title: String = "User not found"
        override val detail: Option[String] = Some(username)
    }
}