package services.messages

import java.util.{UUID, Date}
import javax.inject.{Inject, Singleton}
import dao.{ConversationDAO, MessageDAO}
import models.{Conversation, Message}

@Singleton
class MessageService @Inject()(messageDAO: MessageDAO, conversationDAO: ConversationDAO) {

    def saveMessage(message: Message, userKey: UUID): Message = {
        conversationDAO.findByBothUsers(message.senderKey, userKey).headOption match {
            case Some(conversation) =>
                val messageToSave = message.copy(conversationKey = conversation.key)
                messageDAO.save(messageToSave)
            case None =>
                val newConversation = Conversation(UUID.randomUUID(), message.senderKey, userKey, new Date())
                val messageToSave = message.copy(conversationKey = newConversation.key)
                messageDAO.save(messageToSave)
        }
    }



}
