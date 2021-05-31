package org.education.doobie.domain

import org.education.protocols.UserProtocol.User

trait CommonRepositoryAlgebra[F[_]] {
  def createUser(user: User): F[Int]

  def getAllUsers: F[List[User]]

}
