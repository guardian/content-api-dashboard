package controllers

import com.gu.googleauth.GoogleAuthConfig
import models._
import play.api.mvc._
import services.FortuneTeller

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class Application(environments: Seq[Environment], fortuneTeller: FortuneTeller, val authConfig: GoogleAuthConfig) extends Controller with AuthActions {

  def index = AuthAction.async { request =>
    Future.traverse(environments)(env => fortuneTeller.whatIsMySpiritAnimal(env.stagePrefix, env.stack).map(animal => (env, animal))) map { envsAndAnimals =>
      val panels = envsAndAnimals.map { case (env, animal) => Panel(env, animal) }
      Ok(views.html.index(panels, request.user.firstName))
    }
  }

  def healthcheck = Action { Ok("OK") }

}
