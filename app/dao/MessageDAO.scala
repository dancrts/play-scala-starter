package dao

import models.Message
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.{Date, UUID}
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MessageDAO  @Inject()(
                               @NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider
                           )
                           (
                               implicit executionContext: ExecutionContext
                           )
    extends HasDatabaseConfigProvider[JdbcProfile]{

    import profile.api._

    private class MessageTable (tag: Tag) extends Table[Message](tag, "Message") {

        implicit val dateColumnType: MessageDAO.this.profile.BaseColumnType[java.util.Date] = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

        def key: Rep[UUID] = column[UUID]("MESSAGE_KEY", O.PrimaryKey)

        def content: Rep[String] = column[String]("CONTENT")

        def senderKey: Rep[UUID] = column[UUID]("SENDER_KEY")

        def receiverKey: Rep[UUID] = column[UUID]("RECEIVER_KEY")

        def createdAt: Rep[Date] = column[Date]("CREATED_AT")

        override def * : ProvenShape[Message] = (key, content, senderKey, receiverKey, createdAt) <> (Message.tupled, Message.unapply _)
    }


}
