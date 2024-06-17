package dao

import models.Account
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.{Date, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class AccountDAO @Inject() (@NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

    import profile.api._

    private class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {

        implicit val dateColumnType: AccountDAO.this.profile.BaseColumnType[java.util.Date] = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

        def accountKey: Rep[UUID] = column[UUID]("account_key", O.PrimaryKey)

        def fullname: Rep[String] = column[String]("fullname")

        def email: Rep[String] = column[String]("email", O.Unique)

        def password: Rep[String] = column[String]("password")

        def profilePic: Rep[Option[UUID]] = column[Option[UUID]]("profile_pic")

        override def * : ProvenShape[Account] = (accountKey, fullname, email, password, profilePic) <> (Account.tupled, Account.unapply _)
    }

    private val accountTable = TableQuery[AccountTable]

    def createAccount(account: Account): Account = {
        val newAccount = account.copy(password = encryptPw(account.password))
        val insertedAccountQuery = accountTable.returning(accountTable) += newAccount
        Await.result(db.run(insertedAccountQuery),Duration.Inf)
    }

    def updateAccount(account: Account): Future[Int] = {
        val updateQuery = accountTable.filter(_.accountKey === account.accountKey).update(account)
        db.run(updateQuery)
    }

    def updateToNewPassword(accKey: String, newPw: String, oldPw: String): Future[Int] = {
        val accountID = UUID.fromString(accKey)
        val maybeAccount = findByKey(accountID)
        maybeAccount match {
            case Some(account) =>
                if (BCrypt.checkpw(oldPw, account.password)) {
                    updatePw(accountID, newPw)
                } else {
                    Future.never
                }
            case None => Future.never
        }
    }

    def findAndValidate(email: String, password: String): Option[Account] = {
        val maybeAccount = findByEmail(email)
        maybeAccount match {
            case Some(foundAccount) =>
               checkPw(foundAccount, password)
            case None => None
        }
    }

    def findByEmail(email: String): Option[Account] = {
        val findQuery = accountTable.filter(_.email === email).result.headOption
        Await.result(db.run(findQuery),Duration.Inf)
    }

    def findByKey(accountKey: UUID): Option[Account] = {
        val findQuery = accountTable.filter(_.accountKey === accountKey).result.headOption
        Await.result(db.run(findQuery), Duration.Inf)
    }

    private def updatePw(accId: UUID, newPw: String): Future[Int] = {
        val encryptedPw = encryptPw(newPw)
        val updatePwQuery = accountTable.filter(_.accountKey === accId).map(_.password).update(encryptedPw)
        db.run(updatePwQuery)
    }

    private def checkPw(account: Account, password: String): Option[Account] = {
        if (BCrypt.checkpw(password, account.password)) Some(account)
        else None
    }

    private def encryptPw(pw: String): String = BCrypt.hashpw(pw, BCrypt.gensalt())
}
