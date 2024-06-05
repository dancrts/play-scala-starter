package dao

import models.Account
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape
import sun.security.util.Password

import java.util.{Date, UUID}
import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class AccountDAO @Inject() (@NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider)
                           (implicit executionContext: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {

    import profile.api._

    implicit val dateColumnType: AccountDAO.this.profile.BaseColumnType[Date] = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    private class AccountTable(tag: Tag) extends Table[Account](tag, "Account") {
        def accountKey: Rep[UUID] = column[UUID]("ACCOUNT_KEY", O.PrimaryKey)
        def fullname: Rep[String] = column[String]("FULL_NAME")
        def email: Rep[String] = column[String]("EMAIL", O.Unique)
        def password: Rep[String] = column[String]("Password")
        def createdAt: Rep[Date] = column[Date]("CREATED_AT")
        def profilePic: Rep[UUID] = column[UUID]("PROFILE_PIC")

        override def * : ProvenShape[Account] = (accountKey, fullname, email, password, createdAt, profilePic) <> (Account.tupled, Account.unapply)
    }

    private val accountTable = TableQuery[AccountTable]

    def create(account: Account): Future[Account] = {
        val newAccount = account.copy(password = encryptPw(account.password))
        val insertedAccountQuery = accountTable.returning(accountTable) += newAccount
        db.run(insertedAccountQuery)
    }

    private def findByEmail(email: String): Option[Account] = {
        val findQuery = accountTable.filter(_.email === email).result.headOption
        Await.result(db.run(findQuery),Duration.Inf)
    }

    private def findByKey(accountKey: UUID): Option[Account] = {
        val findQuery = accountTable.filter(_.accountKey === accountKey).result.headOption
        Await.result(db.run(findQuery), Duration.Inf)
    }

    def updateAccount(account: Account): Future[Int] = {
        val updateQuery = accountTable.filter(_.accountKey === account.accountKey).update(account)
        db.run(updateQuery)
    }

    def addProfilePicture(accKey: String, picKey: String) = {

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
