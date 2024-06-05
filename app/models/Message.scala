package models

import java.util.{UUID, Date}

case class Message(
                      key: UUID,
                      content: String,
                      senderKey: UUID,
                      receiverKey: UUID,
                      insertedAt: Date
                  )

object Message {
    def unapply(m: Message): Option[(UUID, String, UUID, UUID, Date)] =
        Some((m.key, m.content, m.senderKey, m.receiverKey, m.insertedAt))
    def tupled: ((UUID, String, UUID, UUID, Date)) =>
        Message = (this.apply _).tupled
}