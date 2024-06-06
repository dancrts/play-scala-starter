package controllers.api.chat

import play.api.mvc.{Action, AnyContent, WebSocket}

trait ChatController {
    def getMessage: Action[AnyContent]

    def socket: WebSocket
}
