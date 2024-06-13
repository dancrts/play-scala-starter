package services.account

import com.fasterxml.uuid.Generators

import java.util.UUID
import javax.inject.{Inject, Singleton}
import com.qrsof.jwt.models.{DecodedToken, JwtToken}
import com.qrsof.jwt.validation.JwtValidationService
import dao.AccountDAO
import models.Account
import services.account.dto.{AuthRequest, GoogleLoginRequest}
import services.account.AccountException
import services.account.AccountException._

@Singleton
class AccountService @Inject()(accountDAO: AccountDAO) {

    def logIn(loginReq: AuthRequest): Either[Account, AccountException] = {
        accountDAO.findAndValidate(loginReq.email, loginReq.password) match {
            case Some(account) => Left(account)
            case None => Right(AccountNotFoundException(loginReq.email))

        }
    }

    def register(authReq: AuthRequest): Either[AccountException, UUID] = {
        val newAccount = Account(generateKey(), authReq.email, authReq.email, authReq.password, None)
        accountDAO.findByEmail(authReq.email) match {
            case Some(value) => Left(UserAlreadyExistsException(authReq.email))
            case None =>
                val createdAcc = accountDAO.createAccount(newAccount)
                Right(createdAcc.accountKey)
        }


    }

    def googleLogin(gLoginRequest: GoogleLoginRequest): Unit = {
        println(gLoginRequest)
//        accountDAO.findByEmail(gLoginRequest.userInformation.email) match {
//            case Some(account) => println(account)
//            case None => println("No acc")
//        }
    }

    private def generateKey(): UUID = Generators.timeBasedGenerator().generate()
}
