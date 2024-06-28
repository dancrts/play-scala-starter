package dao

import models.Workspace
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.{Date, UUID}
import javax.inject.Inject
import scala.concurrent.ExecutionContext


class WorkspaceDAO @Inject()(@NamedDatabase("chaapy") protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile]{

    import profile.api._

    class WorkspaceTable(tag: Tag) extends Table[Workspace](tag, "workspaces") {

        implicit val dateColumnType: WorkspaceDAO.this.profile.BaseColumnType[java.util.Date] = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

        def key: Rep[UUID] = column[UUID]("WORKSPACE_KEY", O.PrimaryKey)

        def name: Rep[String] = column[String]("NAME")

        def description: Rep[Option[String]] = column[Option[String]]("DESCRIPTION")

        def imageKey: Rep[Option[UUID]] = column[Option[UUID]]("IMAGE")

        def color: Rep[String] = column[String]("COLOR")

        def modifiedAt: Rep[Date] = column[Date]("MODIFIED_AT")

        def createdAt: Rep[Date] = column[Date]("CREATED_AT")

        override def * : ProvenShape[Workspace] = (key, name, description, imageKey, color, modifiedAt, createdAt) <> (Workspace.tupled, Workspace.unapply _)
    }

    private val workspaceTable = TableQuery[WorkspaceTable]

    def createWorkspace(workspace: Workspace): Workspace = ???

    def updateWorkspace(workspace: Workspace) = ???

    def updateIcon(imageKey: UUID) = ???

    def deleteWorkspace(workspaceKey: UUID) = ???


}
