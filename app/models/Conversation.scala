package models

import java.util.UUID

case class Conversation(key: UUID, participant1: UUID, participant2: UUID)

object Conversation {

    def unapply(conv: Conversation): Option[(UUID, UUID, UUID)] =
        Some((conv.key, conv.participant1, conv.participant2))

    def tupled: ((UUID, UUID, UUID)) =>
        Conversation = (this.apply _).tupled
}