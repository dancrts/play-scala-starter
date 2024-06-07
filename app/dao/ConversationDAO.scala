package dao

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.{Date, UUID}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import models.Conversation

class ConversationDAO @Inject()(@NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile]{

    import profile.api._

    private class ConversationTable(tag: Tag) extends Table[Conversation](_tableTag = tag, _tableName = "Conversation") {

        def key: Rep[UUID] = column[UUID]("CONVERSATION_KEY", O.PrimaryKey)

        def participant1: Rep[UUID] = column[UUID]("PARTICIPANT_ONE")

        def participant2: Rep[UUID] = column[UUID]("PARTICIPANT_TWO")

        override def * : ProvenShape[Conversation] = ???
    }

    private val conversationTable = TableQuery[ConversationTable]

}
