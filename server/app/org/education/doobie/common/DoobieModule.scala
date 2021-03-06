package org.education.doobie.common

import cats.effect._
import doobie.Transactor
import org.education.doobie.config.DatabaseConfig
import org.education.doobie.repository.ParsonRepositoryInterpreter
import play.api.Configuration

class DoobieModule[F[_] : Effect : ContextShift](configuration: Configuration) {
  val transactor: Transactor[F] = DatabaseConfig.dbTransactor(configuration)
  val parsonRepo = new ParsonRepositoryInterpreter[F](transactor)
}
