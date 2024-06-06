package models

import java.util.{UUID, Date}

case class Message(key: UUID, content: String, senderKey: UUID, receiverKey: UUID, createdAt: Date)

object Message {
    def unapply(m: Message): Option[(UUID, String, UUID, UUID, Date)] =
        Some((m.key, m.content, m.senderKey, m.receiverKey, m.createdAt))

    def tupled: ((UUID, String, UUID, UUID, Date)) =>
        Message = (this.apply _).tupled
}