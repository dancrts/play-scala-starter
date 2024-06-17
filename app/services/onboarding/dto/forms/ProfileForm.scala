package services.onboarding.dto.forms

case class ProfileForm(
                          userKey: String,
                          firstName: String,
                          lastName: String,
                          profileURL: Option[String]
                      )

