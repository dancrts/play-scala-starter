package controllers

import org.apache.pekko.actor._
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Flow
import play.api.libs.streams.ActorFlow
import javax.inject._
import play.api._
import play.api.libs.json._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends BaseController {
    /**
     * Create an Action to render an HTML page.
     *
     * The configuration in the `routes` file means that this method
     * will be called when the application receives a `GET` request with
     * a path of `/`.
     */
    def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.index())
    }

    def webhook(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
        val mode = request.getQueryString("hub.mode")
        val challenge = request.getQueryString("hub.challenge")
        val token = request.getQueryString("hub.verify_token")
        println(s"Received webhook with body: $mode")
        challenge match {
            case Some(value) => Ok(value)
            case None => Forbidden
        }
    }

    def getMessage: Action[AnyContent] = Action { request: Request[AnyContent] =>
        println(request.body.toString)

        Ok("It works!")
    }

    def options: Action[AnyContent] = Action { request =>
        Ok
    }

    def socket: WebSocket = WebSocket.accept[String, String] { request =>
        ActorFlow.actorRef { out => MyWebSocketActor.props(out) }
    }

}

object MyWebSocketActor {
    def props(out: ActorRef): Props = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
    def receive: Receive = {
        case msg: String =>
            out ! ("I received your message: " + msg)
    }
}
