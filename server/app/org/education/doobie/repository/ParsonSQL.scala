package org.education.doobie.repository

import java.time._
import java.sql.Timestamp
import doobie.{LogHandler, Update0}
import org.education.protocols.UserProtocol.{Course, User}
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.Read

object ParsonSQL extends CommonParsonSql {
  implicit val han: LogHandler = LogHandler.jdkLogHandler

  private def ldTime2Timestamp(ldTime: LocalDateTime): Timestamp = {
    Timestamp.valueOf(ldTime)
  }
  implicit val userRead: Read[User] =
    Read[(Timestamp, String, String, String, Option[String])].map {
      case (createdAt, name, email, password, phone) =>
        User(createdAt.toLocalDateTime, name, email, password, phone)
    }

  implicit val courseRead: Read[Course] =
    Read[(Timestamp, String, String, String, String)].map {
      case (createdAt, title, category, video, description) =>
        Course(createdAt.toLocalDateTime, title, category, video, description)
    }

  override def createUser(user: User): Update0 = {
    sql"""insert into users (created_at, name, email, password, phone)
         values (${ldTime2Timestamp(user.createdAt)},${user.name},${user.email},${user.password}, ${user.phone})"""
      .update
  }

  override def createCourse(course: Course): Update0 = {
    sql"""insert into courses (created_at, title, category, video, description)
         values (${ldTime2Timestamp(course.createdAt)},${course.title},${course.category},${course.video}, ${course.description})"""
      .update
  }

  override def getAllUsers: doobie.ConnectionIO[List[User]] = {
    sql"select created_at, name, email, password, phone from users".query[User].to[List]
  }

  override def getAllCourses: doobie.ConnectionIO[List[Course]] = {
    sql"select created_at, title, category, video, description from courses".query[Course].to[List]
  }
}
