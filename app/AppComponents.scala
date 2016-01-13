import com.gu.googleauth.GoogleAuthConfig
import controllers.{ Auth, Application }
import models._
import org.joda.time.Duration
import play.api.ApplicationLoader.Context
import play.api.inject.{ NewInstanceInjector, SimpleInjector }
import play.api.libs.json.Json
import play.api.libs.ws.ning.NingWSComponents
import play.api.{ Logger, BuiltInComponentsFromContext }
import play.api.routing.Router
import router.Routes

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with NingWSComponents {

  val environments = Environments.buildEnvironments(configuration)
  Logger.info(s"Generated ${environments.size} environments.")
  for (env <- environments) Logger.info(Json.toJson(env).toString())

  def missingKey(description: String) = sys.error(s"$description missing. You can create an OAuth 2 client from the Credentials section of the Google dev console.")
  val googleAuthConfig = GoogleAuthConfig(
    clientId = configuration.getString("google.clientId") getOrElse missingKey("OAuth 2 client ID"),
    clientSecret = configuration.getString("google.clientSecret") getOrElse missingKey("OAuth 2 client secret"),
    redirectUrl = configuration.getString("google.redirectUrl") getOrElse missingKey("OAuth 2 callback URL"),
    domain = Some("guardian.co.uk"),
    maxAuthAge = Some(Duration.standardDays(90)),
    enforceValidity = true
  )

  val appController = new Application(environments, googleAuthConfig)
  val authController = new Auth(googleAuthConfig, wsApi)

  val assets = new controllers.Assets(httpErrorHandler)
  val router: Router = new Routes(httpErrorHandler, appController, authController, assets)

}
