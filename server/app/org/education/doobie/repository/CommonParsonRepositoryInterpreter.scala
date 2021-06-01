package org.education.doobie.repository

import cats.effect.Bracket
import doobie._
import doobie.implicits._
import org.education.doobie.domain.ParsonRepositoryAlgebra
import org.education.protocols.UserProtocol.{Course, User}

abstract class CommonParsonRepositoryInterpreter[F[_] : Bracket[*[_], Throwable]](val xa: Transactor[F])
  extends ParsonRepositoryAlgebra[F] {

  val commonSql: CommonParsonSql

  override def createUser(user: User): F[Int] = {
    commonSql.createUser(user).run.transact(xa)
  }
  override def createCourse(course: Course): F[Int] = {
    commonSql.createCourse(course).run.transact(xa)
  }

  override def getAllUsers: F[List[User]] = {
    commonSql.getAllUsers.transact(xa)
  }

  override def getAllCourses: F[List[Course]] = {
    commonSql.getAllCourses.transact(xa)
  }

}
