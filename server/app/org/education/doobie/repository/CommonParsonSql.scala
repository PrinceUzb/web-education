package org.education.doobie.repository

import doobie._
import org.education.protocols.UserProtocol.{Course, User}

trait CommonParsonSql {
  def createUser(user: User): Update0
  def createCourse(course: Course): Update0

  def getAllUsers: doobie.ConnectionIO[List[User]]
  def getAllCourses: doobie.ConnectionIO[List[Course]]

}
