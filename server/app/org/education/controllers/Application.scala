package org.education.controllers

import com.typesafe.scalalogging.LazyLogging
import org.education.doobie.common.DoobieUtil
import org.education.protocols.UserProtocol.User
import org.webjars.play.WebJarsUtil
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc._
import views.html._

import java.time.LocalDateTime
import javax.inject._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.Random

@Singleton
class Application @Inject()(val controllerComponents: ControllerComponents,
                            val configuration: Configuration,
                            indexTemplate: index,
                            loginTemplate: login,
                            adminTemplate: admin,
                            implicit val webJarsUtil: WebJarsUtil)
                           (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  private val DoobieModule = DoobieUtil.doobieModule(configuration)
  val Email = "Maftuna@webuni.uz"
  val Pass = "123"
  private val LoginSessionKey = "education.login"
  private val SessionDuration = Some(90.minutes)

  case class LoginForm
  (
    email: String = "",
    password: String = ""
  )

  private def expiresAtSessionAttrName(sessionAttr: String) = s"$sessionAttr.exp"

  implicit val loginFormFormat: OFormat[LoginForm] = Json.format[LoginForm]

  implicit val loginPlayForm: Form[LoginForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  }

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
    DoobieModule.parsonRepo.createUser(User(LocalDateTime.now, name, email, pass)).unsafeToFuture().map { _ =>
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

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(loginTemplate())
  }

  def checkLogin(email: String, password: String): Boolean = {
    email == Email && password == Pass
  }

  def authInit(sessionAttrName: String,
               sessionAttrVal: String,
               sessionDuration: Option[FiniteDuration] = None): Seq[(String, String)] = {
    val expiresAtSessionAttr = expiresAtSessionAttrName(sessionAttrName)
    sessionDuration.foldLeft(Map(sessionAttrName -> sessionAttrVal)) { (acc, sessionDur) =>
      val nextExpiration = System.currentTimeMillis() + sessionDur.toMillis
      acc + (expiresAtSessionAttr -> nextExpiration.toString)
    }.toSeq
  }

  def loginPost: Action[AnyContent] = Action { implicit request =>
    loginPlayForm.bindFromRequest.fold(
      errorForm => {
        println(s"errorForm: $errorForm")
        Redirect(org.education.controllers.routes.Application.login).flashing("error" -> "Iltimos hammasi to'g'ri ekanini tekshiring!")
      }, {
        case LoginForm(email, password) if checkLogin(email, password) =>
          Redirect(org.education.controllers.routes.Application.admin).addingToSession(
            authInit(LoginSessionKey, email, SessionDuration): _*
          )
        case _ =>
          Redirect(org.education.controllers.routes.Application.login).flashing("error" -> "Login yoki parol xato!")
      })
  }

  def admin: Action[AnyContent] = Action {
    Ok(adminTemplate())
  }

  def generatePass: String = new Randoms().alphanumeric.take(7).mkString
}
