package services.account.dto

case class UserInformation(email: String, name: String, photoUrl: String)

object UserInformation {
    def unapply(userInfo: UserInformation): Option[(String, String, String)] = Some((userInfo.email, userInfo.name, userInfo.photoUrl))
}

