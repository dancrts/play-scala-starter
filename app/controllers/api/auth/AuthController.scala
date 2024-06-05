package controllers.api.auth

import play.api.mvc.{Action, AnyContent}

trait AuthController {
    def login: Action[AnyContent]
}
