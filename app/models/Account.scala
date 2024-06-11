package models

import java.util.{Date, UUID}

case class Account(accountKey: UUID, fullname: String, email: String, password: String, profilePic: Option[UUID] )

object Account {

    def unapply(acc: Account): Option[(UUID, String, String, String,  Option[UUID])] =
        Some((acc.accountKey, acc.fullname, acc.email, acc.password, acc.profilePic))

    def tupled: ((UUID, String, String, String,  Option[UUID])) =>
        Account = (this.apply _).tupled
}