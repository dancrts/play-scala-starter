package services.onboarding.dto.forms

case class ProfileForm(
                          userKey: String,
                          firstName: String,
                          lastName: String,
                          profileURL: Option[String]
                      )
object ProfileForm {
    
    def unapply(prof: ProfileForm): Option[(String, String, String, Option[String])] =
        Some((prof.userKey, prof.firstName, prof.lastName, prof.profileURL))
}
