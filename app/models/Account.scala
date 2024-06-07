package models

import java.util.{Date, UUID}

case class Account(accountKey: UUID, fullname: String, email: String, password: String, createdAt: Date, profilePic: Option[UUID] )

object Account {

    def unapply(acc: Account): Option[(UUID, String, String, String, Date,  Option[UUID])] =
        Some((acc.accountKey, acc.fullname, acc.email, acc.password, acc.createdAt, acc.profilePic))

    def tupled: ((UUID, String, String, String, Date,  Option[UUID])) =>
        Account = (this.apply _).tupled
}