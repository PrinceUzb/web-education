package org.prince.education.doobie.repository

import cats.effect.Bracket
import cats.implicits._
import doobie._
import org.prince.education.doobie.domain.ParsonRepositoryAlgebra

class ParsonRepositoryInterpreter[F[_] : Bracket[*[_], Throwable]](override val xa: Transactor[F])
  extends CommonParsonRepositoryInterpreter[F](xa) with ParsonRepositoryAlgebra[F] {
  override val commonSql: CommonParsonSql = ParsonSQL
}

object ParsonRepositoryInterpreter {
  def apply[F[_] : Bracket[*[_], Throwable]](xa: Transactor[F]): ParsonRepositoryInterpreter[F]
  = new ParsonRepositoryInterpreter(xa)
}