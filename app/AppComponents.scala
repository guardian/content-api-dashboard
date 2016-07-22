import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{ AWSCredentialsProviderChain, InstanceProfileCredentialsProvider }
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2AsyncClient
import com.gu.googleauth.GoogleAuthConfig
import controllers.{ Application, Auth }
import models._
import org.joda.time.Duration
import play.api.ApplicationLoader.Context
import play.api.inject.{ NewInstanceInjector, SimpleInjector }
import play.api.libs.json.Json
import play.api.libs.ws.ning.NingWSComponents
import play.api.{ BuiltInComponentsFromContext, Logger }
import play.api.routing.Router
import services.FortuneTeller

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

  val fortuneTeller = {
    val credsProvider = new AWSCredentialsProviderChain(
      new InstanceProfileCredentialsProvider(),
      new ProfileCredentialsProvider("capi")
    )
    val region = configuration.getString("aws.region") getOrElse sys.error("aws.region not set")
    val ec2client: AmazonEC2AsyncClient = new AmazonEC2AsyncClient(credsProvider).withRegion(Regions.fromName(region))
    new FortuneTeller(ec2client)
  }
  val appController = new Application(environments, fortuneTeller, googleAuthConfig)
  val authController = new Auth(googleAuthConfig, wsApi)

  val assets = new controllers.Assets(httpErrorHandler)
  val router: Router = new Routes(httpErrorHandler, appController, authController, assets)

}
