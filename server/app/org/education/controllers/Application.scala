package org.education.controllers

import com.typesafe.scalalogging.LazyLogging
import org.education.doobie.common.DoobieUtil
import org.education.protocols.UserProtocol.{Course, User}
import org.webjars.play.WebJarsUtil
import play.api.{Configuration, Environment, Mode}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.Files
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc._
import views.html._

import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.matching.Regex
import scala.util.{Random, Try}

@Singleton
class Application @Inject()(val controllerComponents: ControllerComponents,
                            val configuration: Configuration,
                            environment: Environment,
                            indexTemplate: index,
                            loginTemplate: login,
                            adminTemplate: admin,
                            coursesTemplate: courses,
                            implicit val webJarsUtil: WebJarsUtil)
                           (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {
  val filePath = configuration.get[String]("temp-file-path")
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

  def courses: Action[AnyContent] = Action.async {
    DoobieModule.parsonRepo.getAllCourses.unsafeToFuture().map { courses =>
      Ok(coursesTemplate(courses))
    }
  }

  def contactUs: Action[JsValue] = Action(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    println(name)
    Ok(Json.toJson("OK"))
  }

  def createCourse: Action[MultipartFormData[Files.TemporaryFile]]
  = Action.async(parse.multipartFormData) { implicit request =>
    request.body
      .file("video").map { file =>
      Try{
        val body = request.body.asFormUrlEncoded
        val title = body.get("title").flatMap(_.headOption).getOrElse("")
        val category = body.get("category").flatMap(_.headOption).getOrElse("")
        val description = body.get("description").flatMap(_.headOption).getOrElse("")
        val videoName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".mp4"
        DoobieModule.parsonRepo.createCourse(Course(LocalDateTime.now, title, category, videoName, description)).unsafeToFuture().map { _ =>
          file.ref.copyTo(Paths.get(videoName), replace = true)
          Redirect(org.education.controllers.routes.Application.admin).flashing("success" -> "Muvofaqiyatli qo'shildi!")
        }
      } getOrElse {
        Future.successful(Redirect(org.education.controllers.routes.Application.admin).flashing("error" -> "Iltimos hammasi to'g'ri ekanini tekshiring!"))
      }

      }.getOrElse {
        Future.successful(Redirect(org.education.controllers.routes.Application.admin).flashing("error" -> "Video yuklashda xatolik yuz berdi!"))
      }
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

  def admin: Action[AnyContent] = Action { implicit request =>
    Ok(adminTemplate())
  }
  val AbsolutePath: Regex = """^(/|[a-zA-Z]:\\).*""".r
  def at(rootPath: String, file: String): Action[AnyContent] = Action.async {
    Future {
      val fileToServe = rootPath match {
        case AbsolutePath(_) => new File(rootPath, file)
        case _               => new File(environment.getFile(rootPath), file)
      }
      println(fileToServe)
      if (fileToServe.exists) {
        println(fileToServe + "555")

        Ok.sendFile(fileToServe, inline = true).withHeaders(CACHE_CONTROL -> "max-age=3600")
      } else {
        NotFound
      }
    }
    }
  def generatePass: String = new Randoms().alphanumeric.take(7).mkString
}
