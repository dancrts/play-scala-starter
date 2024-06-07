package services.account

import dao.AccountDAO
import services.account.dto.{GoogleLoginRequest, AuthRequest}
import com.fasterxml.uuid.Generators
import models.Account

import java.util.{Date, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class AccountService @Inject()(accountDAO: AccountDAO) {

    def logIn(loginReq: AuthRequest): Unit = {
        accountDAO.findAndValidate(loginReq.email, loginReq.password) match {
            case Some(account) => println("Logged in")
            case None => println("none")
        }
    }

    def register(authReq: AuthRequest): UUID = {
        val newAccount = Account(generateKey(), authReq.email, authReq.email, authReq.password, new Date(), None)
        val createdAcc = Await.result(accountDAO.createAccount(newAccount), Duration.Inf)
        println(createdAcc)
        createdAcc.accountKey
    }

    def googleLogin(gLoginRequest: GoogleLoginRequest): Unit = {

    }

    private def generateKey(): UUID = Generators.timeBasedGenerator().generate()
}
