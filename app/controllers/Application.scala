package controllers

import com.gu.googleauth.GoogleAuthConfig
import models._
import play.api.mvc._
import services.PanelBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Application(environments: Seq[Environment], panelBuilder: PanelBuilder, val authConfig: GoogleAuthConfig) extends Controller with AuthActions {

  def index = AuthAction.async { request =>
    val panels = Future.traverse(environments)(panelBuilder.makePanel)
    panels map { ps =>
      Ok(views.html.index(ps, request.user.firstName))
    }
  }

  def healthcheck = Action { Ok("OK") }

}
