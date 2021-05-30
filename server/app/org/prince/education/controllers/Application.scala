package org.prince.education.controllers

import com.typesafe.scalalogging.LazyLogging
import org.prince.education.doobie.common.DoobieUtil
import org.prince.education.protocols.UserProtocol.User
import org.webjars.play.WebJarsUtil
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import views.html.index

import java.time.LocalDateTime
import javax.inject._
import scala.concurrent.ExecutionContext
import scala.util.Random

@Singleton
class Application @Inject()(val controllerComponents: ControllerComponents,
                            val configuration: Configuration,
                            indexTemplate: index,
                            implicit val webJarsUtil: WebJarsUtil)
                           (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  private val DoobieModule = DoobieUtil.doobieModule(configuration)

  def index: Action[AnyContent] = Action {
    Ok(indexTemplate())
  }

  def contactUs: Action[JsValue] = Action(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    println(name)
    Ok(Json.toJson("OK"))
  }

  def register: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    val email = (request.body \ "email").as[String]
    val pass = generatePass
    println(name)
    DoobieModule.parsonRepo.createUser(User(LocalDateTime.now, name, email, pass)).unsafeToFuture().map{ _ =>
      Ok(Json.toJson(s"Sizning maxfiy so'zingiz: $pass"))
    }
  }

  class Randoms extends Random {
    override def alphanumeric: LazyList[Char] = {
      def nextAlphaNum: Char = {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        chars charAt (Random nextInt chars.length)
      }

      LazyList continually nextAlphaNum
    }
  }
  def generatePass: String = new Randoms().alphanumeric.take(7).mkString

}
