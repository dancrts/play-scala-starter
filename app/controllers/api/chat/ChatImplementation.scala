package controllers.api.chat

import javax.inject._
import play.api.mvc._
import play.api.libs.streams.ActorFlow
import org.apache.pekko.actor.{ActorSystem, Props}
import org.apache.pekko.stream.Materializer

import actors.chat._

@Singleton
class ChatImplementation @Inject()(val controllerComponents: ControllerComponents) (
    implicit system: ActorSystem, mat: Materializer
) extends ChatController with BaseController{

    private val manager = system.actorOf(Props[ChatManager](), "Manager")

    def getMessage: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        println(request.body.asJson.get)

        request.body.asJson match {
            case Some(value) =>
                manager ! ChatManager.Message(value.toString())
        }

        Ok("It works!")
    }

    def socket: WebSocket = WebSocket.accept[String, String] { _ =>
        ActorFlow.actorRef { out =>
            ChatActor.props(out, manager)
        }
    }
}
