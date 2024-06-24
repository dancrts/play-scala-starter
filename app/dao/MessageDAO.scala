package dao

import models.Message
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.{Date, UUID}
import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class MessageDAO  @Inject()(@NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

    import profile.api._

    private class MessageTable (tag: Tag) extends Table[Message](tag, "messages") {

        implicit val dateColumnType: MessageDAO.this.profile.BaseColumnType[java.util.Date] = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

        def key: Rep[UUID] = column[UUID]("MESSAGE_KEY", O.PrimaryKey)

        def content: Rep[String] = column[String]("CONTENT")

        def senderKey: Rep[UUID] = column[UUID]("SENDER_KEY")

        def conversationKey: Rep[UUID] = column[UUID]("CONVERSATION_KEY")

        def createdAt: Rep[Date] = column[Date]("CREATED_AT")

        override def * : ProvenShape[Message] = (key, content, senderKey, conversationKey, createdAt) <> (Message.tupled, Message.unapply _)
    }
    private val messageTable = TableQuery[MessageTable]

    def save(message: Message): Message = {
        val insertQuery = messageTable.returning(messageTable) += message
        Await.result(db.run(insertQuery),Duration.Inf)
    }

    def updateMessage(message: Message) = {
        val updateQuery = messageTable.filter(_.key === message.key).update(message)
        Await.result(db.run(updateQuery), Duration.Inf)
    }

    def deleteMessage(messageKey: UUID) = {
        val deleteQuery = messageTable.filter(_.key === messageKey).delete
        Await.result(db.run(deleteQuery), Duration.Inf)
    }

    def getAll(convKey: UUID): Seq[Message] = {
        val searchQuery = messageTable.filter(_.conversationKey === convKey).result
        Await.result(db.run(searchQuery) ,Duration.Inf)
    }
}
