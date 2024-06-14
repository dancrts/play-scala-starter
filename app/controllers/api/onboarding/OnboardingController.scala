package controllers.api.onboarding

import play.api.libs.Files
import play.api.mvc.{Action, AnyContent, MultipartFormData}

trait OnboardingController {
    def saveWorkspaceAndProfile: Action[MultipartFormData[Files.TemporaryFile]]

    def getUserMetadata(userKey: String): Action[AnyContent]
}
