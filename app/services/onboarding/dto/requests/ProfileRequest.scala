package services.onboarding.dto.requests

import org.apache.pekko.http.scaladsl.common.StrictForm.FileData

case class ProfileRequest(
                             userKey: String,
                             firstName: String,
                             lastName: String,
                             profileIcon: Option[FileData],
                             profileURL: Option[String]
                         )
