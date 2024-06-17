package services.onboarding.dto.requests

import org.apache.pekko.http.scaladsl.common.StrictForm.FileData

case class WorkspaceRequest(
                               userKey: String,
                               name: String,
                               description: Option[String],
                               icon: Option[FileData],
                               color: String
                           )
