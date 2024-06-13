package services.onboarding.dto

import org.apache.pekko.http.scaladsl.common.StrictForm.FileData

case class WorkspaceAndProfileRequest(
                                         userKey: String,
                                         workspaceName: String,
                                         workspaceDescription: Option[String],
                                         workspaceIcon: Option[FileData],
                                         workspaceColor: String,
                                         profileName: String,
                                         profileLastname: String,
                                         profileIcon: Option[FileData],
                                         profileURL: Option[String]
                                     )
