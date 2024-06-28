package services.account.dto

case class DeviceInformation (platform: String, deviceId: Option[String], deviceDescription: Option[String])

object DeviceInformation {
    def unapply(devInfo: DeviceInformation): Option[(String, Option[String], Option[String])] =
        Some((devInfo.platform, devInfo.deviceId, devInfo.deviceDescription))
}
