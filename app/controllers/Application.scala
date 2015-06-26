package controllers

import models._
import play.api.mvc._
import services.PanelBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Application(environments: Seq[Environment], panelBuilder: PanelBuilder) extends Controller {

  def index = Action.async {
    val panels = Future.traverse(environments)(panelBuilder.makePanel)
    panels map { ps =>
      Ok(views.html.index(ps))
    }
  }

  def healthcheck = Action { Ok("OK") }

}
