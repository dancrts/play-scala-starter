package dao

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.{GetResult, JdbcProfile}
import slick.lifted.ProvenShape

import java.util.{Date, UUID}
import javax.inject.Inject
import scala.concurrent.{Await, ExecutionContext, Future}
import models.Conversation

import scala.concurrent.duration.Duration

class ConversationDAO @Inject()(@NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile]{

    import profile.api._

    private class ConversationTable(tag: Tag) extends Table[Conversation](_tableTag = tag, _tableName = "conversations") {

        implicit val dateColumnType: ConversationDAO.this.profile.BaseColumnType[java.util.Date] = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

        def key: Rep[UUID] = column[UUID]("CONVERSATION_KEY", O.PrimaryKey)

        def participant1: Rep[UUID] = column[UUID]("PARTICIPANT_ONE")

        def participant2: Rep[UUID] = column[UUID]("PARTICIPANT_TWO")

        def createdAt: Rep[Date] = column[Date]("CREATED_AT")

        override def * : ProvenShape[Conversation] = (key, participant1, participant2, createdAt) <> (Conversation.tupled, Conversation.unapply _)
    }

    private val conversationTable = TableQuery[ConversationTable]

    private val formatGetResult: GetResult[Conversation] = GetResult(r =>
        Conversation(
            UUID.fromString(r.<<),
            UUID.fromString(r.<<),
            UUID.fromString(r.<<),
            r.nextDate()
        )
    )

    def createConversation(conversation: Conversation): Conversation = {
        val insertQuery = conversationTable.returning(conversationTable) += conversation
        Await.result(db.run(insertQuery), Duration.Inf)
    }

    def findByUser(participant: UUID): Seq[Conversation] = {
        implicit val getResultConversations: GetResult[Conversation] = formatGetResult

        val searchQuery = sql"""SELECT * FROM "conversations" WHERE participant1 = '$participant' OR participant2 = '$participant'""".as[Conversation]
        Await.result(db.run(searchQuery), Duration.Inf)
    }
    
    def findByBothUsers(user1: UUID, user2: UUID): Seq[Conversation] = {
        implicit val getResultConversations: GetResult[Conversation] = formatGetResult
        val searchQuery = sql"""SELECT FROM "conversations" WHERE (participant1 = '$user1' AND participant2 = '$user2') OR ((participant1 = '$user2' AND participant2 = '$user1')) """.as[Conversation]
        Await.result(db.run(searchQuery),Duration.Inf)
    }

}
