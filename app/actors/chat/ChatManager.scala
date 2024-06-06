package actors.chat

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor._
import scala.collection.mutable.ListBuffer

class ChatManager extends Actor {
    private val chatters = ListBuffer.empty[ActorRef]

    import ChatManager._
    def receive: Receive = {
        case NewChatter(chatter) => chatters += chatter
        case Message(msg)        => for (c <- chatters) c ! ChatActor.SendMessage(msg)
    }
}

object ChatManager {
    case class NewChatter(chatter: ActorRef)
    case class Message(msg: String)
}