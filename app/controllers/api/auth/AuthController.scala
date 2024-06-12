package controllers.api.auth

import play.api.mvc.{Action, AnyContent}

trait AuthController {
    def login: Action[AnyContent]

    def register: Action[AnyContent]

    def loginWithGoogle: Action[AnyContent]
}
