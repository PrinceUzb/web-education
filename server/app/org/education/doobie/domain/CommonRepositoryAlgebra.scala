package org.education.doobie.domain

import org.education.protocols.UserProtocol.{Course, User}

trait CommonRepositoryAlgebra[F[_]] {
  def createUser(user: User): F[Int]
  def createCourse(course: Course): F[Int]

  def getAllUsers: F[List[User]]
  def getAllCourses: F[List[Course]]

}
