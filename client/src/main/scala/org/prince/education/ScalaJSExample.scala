package org.prince.education

import org.querki.jquery._
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js

object ScalaJSExample {

  case class AjaxSuccess(data: js.Any, textStatus: String, jqXHR: JQueryXHR)

  def resCreator: (js.Any, String, JQueryXHR) => AjaxSuccess = (a, b, c) => AjaxSuccess(a, b, c)

  def ajaxSettings(url: String, data: String): JQueryAjaxSettings = {
    def handleAjaxError(jqXHR: JQueryXHR, textStatus: String, errorThrow: String): Unit = {
      println("Error while performing AJAX POST")
    }

    def handleAjaxSuccess(data: js.Any, textStatus: String, jqXHR: JQueryXHR): Unit = {
      val jsonFromServer = jqXHR.responseText
      println(s"Got from server: $jsonFromServer")
    }

    js.Dynamic.literal(
      url = url,
      contentType = "application/json",
      data = data,
      `type` = "POST",
      success = handleAjaxSuccess _,
      error = handleAjaxError _).asInstanceOf[JQueryAjaxSettings]
  }

  def main(args: Array[String]): Unit = {
    val preloader = $("#preloader")
    window.onload = (_: Event) =>
      if (!preloader.length.isNaN) {
        preloader.delay(400).fadeOut(
          JQueryAnimationSettings.duration(1000)
            .complete { elem =>
              elem.parentNode.removeChild(elem)
            })
      }
    $(".set-bg").each { el =>
      val htmlEl = el.asInstanceOf[HTMLElement]
      val bg = el.getAttribute("data-setbg")
      htmlEl.style.backgroundImage = "url(" + bg + ")"
    }

    $("#regButton").click { (_: Event) =>
      val name = $("#name").valueString
      val email = $("#email").valueString
      $.ajax(ajaxSettings("/register", s"""{"name": "$name", "email": "$email"}"""))
        .done { (_: js.Any, _: String, res: JQueryXHR) =>
          window.alert(res.responseText)
        }
    }
  }
}
