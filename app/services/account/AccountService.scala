package services.account

import dao.AccountDAO
import services.account.dto.{AuthRequest, GoogleLoginRequest}
import com.fasterxml.uuid.Generators
import models.Account
import io.scalaland.chimney.dsl.TransformerOps

import java.util.{Date, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.impl.Promise
import scala.util.{Failure, Success}

@Singleton
class AccountService @Inject()(accountDAO: AccountDAO) {

    def logIn(loginReq: AuthRequest): Either[Account, String] = {
        accountDAO.findAndValidate(loginReq.email, loginReq.password) match {
            case Some(account) => Left(account)
            case None => Right("No hay ninguna cuenta")
        }
    }

    def register(authReq: AuthRequest): Either[UUID, String] = {
        val newAccount = Account(generateKey(), authReq.email, authReq.email, authReq.password, None)
        accountDAO.findByEmail(authReq.email) match {
            case Some(value) => Right("Imposible de Crear")
            case None =>
                val newAcc = accountDAO.createAccount(newAccount)
                Left(newAcc.accountKey)
        }


    }

    def googleLogin(gLoginRequest: GoogleLoginRequest): Unit = {

    }

    private def generateKey(): UUID = Generators.timeBasedGenerator().generate()
}
