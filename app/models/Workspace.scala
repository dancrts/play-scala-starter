package models

import java.util.{Date, UUID}

case class Workspace(key: UUID, name: String, description: Option[String], imageKey: Option[UUID], color: String, modifiedAt: Date, createdAt: Date)

object Workspace {

    def unapply(w: Workspace): Option[(UUID, String, Option[String], Option[UUID], String, Date, Date)] =
        Some((w.key, w.name, w.description, w.imageKey, w.color, w.modifiedAt, w.createdAt))

    def tupled: ((UUID, String, Option[String], Option[UUID], String, Date, Date)) => Workspace = (this.apply _).tupled
}
