package services.account.dto

case class GoogleLoginRequest(idToken: String, deviceInformation: DeviceInformation, userInformation: UserInformation)

object GoogleLoginRequest{
    def unapply(gReq: GoogleLoginRequest): Option[(String, DeviceInformation, UserInformation)] = Some((gReq.idToken, gReq.deviceInformation , gReq.userInformation))
}