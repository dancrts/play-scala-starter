package services.onboarding.dto.forms

case class WorkspaceForm(
                            userKey: String,
                            name: String,
                            description: Option[String],
                            color: String
                        )