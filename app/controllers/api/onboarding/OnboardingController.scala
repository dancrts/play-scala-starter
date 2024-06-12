package controllers.api.onboarding

import play.api.mvc.{Action, AnyContent}

trait OnboardingController {
    def saveWorkspaceAndProfile: Action[AnyContent]

    def getUserMetadata(userKey: String): Action[AnyContent]
}
