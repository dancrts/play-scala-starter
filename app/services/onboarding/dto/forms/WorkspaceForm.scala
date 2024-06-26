package services.onboarding.dto.forms

case class WorkspaceForm(
                            userKey: String,
                            name: String,
                            description: Option[String],
                            color: String
                        )

object WorkspaceForm {
    def unapply(wsForm: WorkspaceForm): Option[(String, String, Option[String], String)] =
        Some((wsForm.userKey, wsForm.name, wsForm.description, wsForm.color))
}