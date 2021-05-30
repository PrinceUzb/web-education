package org.prince.education.doobie.domain

import org.prince.education.protocols.UserProtocol.User

trait CommonRepositoryAlgebra[F[_]] {
  def createUser(user: User): F[Int]

  def getAllUsers: F[List[User]]

}
