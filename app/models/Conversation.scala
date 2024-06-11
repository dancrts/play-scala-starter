package models

import java.util.{Date, UUID}

case class Conversation(key: UUID, participant1: UUID, participant2: UUID, createdAt: Date)

object Conversation {

    def unapply(conv: Conversation): Option[(UUID, UUID, UUID, Date)] =
        Some((conv.key, conv.participant1, conv.participant2, conv.createdAt))

    def tupled: ((UUID, UUID, UUID, Date)) =>
        Conversation = (this.apply _).tupled
}