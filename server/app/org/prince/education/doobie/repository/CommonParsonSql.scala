package org.prince.education.doobie.repository

import doobie._
import org.prince.education.protocols.UserProtocol.User

trait CommonParsonSql {
  def createUser(user: User): Update0

  def getAllUsers: doobie.ConnectionIO[List[User]]

}
