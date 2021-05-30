package org.prince.education.doobie.repository

import java.time._
import java.sql.Timestamp
import doobie.{LogHandler, Update0}
import org.prince.education.protocols.UserProtocol.User
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.Read

object ParsonSQL extends CommonParsonSql {
  implicit val han: LogHandler = LogHandler.jdkLogHandler

  private def ldTime2Timestamp(ldTime: LocalDateTime): Timestamp = {
    Timestamp.valueOf(ldTime)
  }
  implicit val patientsDocRead: Read[User] =
    Read[(Timestamp, String, String, String, Option[String])].map {
      case (createdAt, name, email, password, phone) =>
        User(createdAt.toLocalDateTime, name, email, password, phone)
    }


  override def createUser(user: User): Update0 = {
    sql"""insert into users (created_at, name, email, password, phone)
         values (${ldTime2Timestamp(user.createdAt)},${user.name},${user.email},${user.password}, ${user.phone})"""
      .update
  }

  override def getAllUsers: doobie.ConnectionIO[List[User]] = {
    sql"select * from users".query[User].to[List]
  }
}
