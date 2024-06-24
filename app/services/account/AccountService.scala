package services.account


import java.util.UUID
import io.scalaland.chimney.dsl.TransformerOps
import javax.inject.{Inject, Singleton}
import com.qrsof.apptack.client.{ApptackClient, ApptackExceptions, DeviceInformation}
import com.qrsof.jwt.models.{DecodedToken, JwtToken}
import com.qrsof.jwt.validation.JwtValidationService
import dao.AccountDAO
import models.Account
import services.account.dto.{AuthRequest, GoogleLoginRequest, UserInformation}
import services.account.AccountException._

@Singleton
class AccountService @Inject()(accountDAO: AccountDAO, apptackClient: ApptackClient, jwtService: JwtValidationService) {

    def logIn(loginReq: AuthRequest): Either[AccountException, JwtToken] = {

        apptackClient.oauth.login(loginReq.email, loginReq.password) match {
            case Left(exception) => Left(convertAppTackToAuthException(exception))
            case Right(token) =>
                val jwtToken: JwtToken = JwtToken(token.accessToken)

                accountDAO.findAndValidate(loginReq.email, loginReq.password) match {
                    case Some(account) =>
                        println(s"found account $account")
                        Right(jwtToken)
                    case None =>
                        Left(InvalidCredentialsException(loginReq.email))
                }
        }

    }

    def register(authReq: AuthRequest): Either[AccountException, JwtToken] = {

        apptackClient.oauth.register(
            username = authReq.email, password = authReq.password, None, None
        ) match {
            case Left(exception) =>
                Left(convertAppTackToAuthException(exception))
            case Right(token) =>

                val jwtToken: JwtToken =            JwtToken(token.accessToken)
                val decodedToken: DecodedToken =    jwtService.validateJwt(jwtToken).toSeq.head
                val userKey: UUID =                 UUID.fromString(decodedToken.subject)
                val newAccount: Account =           Account(userKey, authReq.email, authReq.email, authReq.password, None)

                accountDAO.findByEmail(authReq.email) match {
                    case Some(foundAcc) =>
                        Left(UserAlreadyExistsException(foundAcc.email))
                    case None =>
                        accountDAO.createAccount(newAccount)
                        Right(jwtToken)
                }
        }



    }

    def googleLogin(gLoginRequest: GoogleLoginRequest): Either[AccountException, JwtToken] = {
        val deviceInfo: DeviceInformation = gLoginRequest.deviceInformation.transformInto[DeviceInformation]

        apptackClient.oauth.loginWithGoogle(gLoginRequest.idToken, deviceInfo) match {
            case Left(exception) => Left(convertAppTackToAuthException(exception))
            case Right(token) =>
                val jwtToken: JwtToken =            JwtToken(token.accessToken)
                val userInfo: UserInformation =     gLoginRequest.userInformation

                accountDAO.findByEmail(userInfo.email) match {
                    case Some(account) => println(account)
                    case None =>

                        val decodedToken: DecodedToken =        jwtService.validateJwt(jwtToken).toSeq.head
                        val userKey: UUID =                     UUID.fromString(decodedToken.subject)
                        val unregisteredAccount: Account =      Account(userKey, userInfo.name, userInfo.email, userInfo.email, None)

                        accountDAO.createAccount(unregisteredAccount)
                }
                Right(jwtToken)
        }

    }

    private def convertAppTackToAuthException(appManagerException: ApptackExceptions): AccountException = {
        appManagerException match {
            case ApptackExceptions.AppNotFoundException(value) =>                   AppNotFoundException(value)
            case ApptackExceptions.UserNotFoundException(value) =>                  AccountNotFoundException(value)
            case ApptackExceptions.UserCredentialsException(userResource) =>        InvalidCredentialsException(userResource)
            case ApptackExceptions.UserAlreadyExistsException(userResource) =>      UserAlreadyExistsException(userResource)
            case ApptackExceptions.UnknowException(resource) =>                     UnknownException(resource)
            case ApptackExceptions.MalformedGoogleTokenException(googleToken) =>    MalformedGoogleTokenException(googleToken)
            case ApptackExceptions.MalformedAppleTokenException(appleToken) =>      MalformedGoogleTokenException(appleToken)
            case ApptackExceptions.ApptackConfigurationException(resource) =>       AppTackConfigurationException(resource)
            case ApptackExceptions.ResourceNotFoundException(resource) =>           ResourceNotFoundException(resource)
        }
    }
}
