package org.education.protocols

import java.time.LocalDateTime

object UserProtocol {
  case class User(createdAt: LocalDateTime, name: String, email: String, password: String, phone: Option[String] = None)
}
