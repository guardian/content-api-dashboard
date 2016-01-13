package controllers

import com.gu.googleauth.GoogleAuthConfig
import models._
import play.api.mvc._

import scala.concurrent.Future

class Application(environments: Seq[Environment], val authConfig: GoogleAuthConfig) extends Controller with AuthActions {

  def index = AuthAction.async { request =>
    val panels = environments.map(env => Panel(env))
    Future.successful(Ok(views.html.index(panels, request.user.firstName)))
  }

  def healthcheck = Action { Ok("OK") }

}
