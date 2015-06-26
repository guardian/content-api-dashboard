import controllers.Application
import models._
import play.api.ApplicationLoader.Context
import play.api.libs.json.Json
import play.api.{ Logger, BuiltInComponentsFromContext }
import play.api.routing.Router
import router.Routes
import services.{ EC2InstanceFinder, PanelBuilder }

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) {

  val environments = Environments.buildEnvironments(context.initialConfiguration)
  Logger.info(s"Generated ${environments.size} environments.")
  for (env <- environments) Logger.info(Json.toJson(env).toString())

  val panelBuilder = new PanelBuilder(new EC2InstanceFinder(context.initialConfiguration))

  val appController = new Application(environments, panelBuilder)

  val assets = new controllers.Assets(httpErrorHandler)
  val router: Router = new Routes(httpErrorHandler, appController, assets)

}
