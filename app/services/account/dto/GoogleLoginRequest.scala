package services.account.dto

case class GoogleLoginRequest(idToken: String, deviceInformation: DeviceInformation, userInformation: UserInformation)

