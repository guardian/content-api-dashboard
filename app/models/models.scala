package models

import play.api.libs.json.{ Json, JsString, JsValue, Writes }

trait AwsTagProvider {
  def awsTags: Seq[(String, String)]
}

sealed trait Stage extends AwsTagProvider {
  def stageName: String

  def awsTags = Seq(
    ("Stage", stageName)
  )
}
object Stage {
  implicit val writes = new Writes[Stage] {
    override def writes(stage: Stage): JsValue = JsString(stage.stageName)
  }
}
case object `PROD-VPC` extends Stage { val stageName = "PROD-VPC" }
case object `CODE-VPC` extends Stage { val stageName = "CODE-VPC" }

sealed trait Stack extends AwsTagProvider {
  def stackName: String

  def awsTags = Seq(
    ("Stack", stackName)
  )
}
object Stack {
  implicit val writes = new Writes[Stack] {
    override def writes(stack: Stack): JsValue = JsString(stack.stackName)
  }
}
case object live extends Stack { val stackName = "content-api" }
case object preview extends Stack { val stackName = "content-api-preview" }

sealed trait App extends AwsTagProvider {
  def appName: String

  def awsTags = Seq(
    ("App", appName)
  )
}
case object Attendant extends App { val appName = "attendant" }
case object Concierge extends App { val appName = "concierge" }
case object Elasticsearch extends App { val appName = "elasticsearch" }
case object Porter extends App { val appName = "porter" }

object App {
  val apps = Seq(Attendant, Concierge, Elasticsearch, Porter)
}

case class Environment(
    stage: Stage,
    stack: Stack,
    fastlyUrl: Option[String],
    nightwatchUrl: Option[String],
    pubflowUrl: Option[String],
    masheryUrl: Option[String],
    apiIndexerUrl: Option[String],
    publicConciergeUrl: Option[String],
    internalConciergeUrl: Option[String]) extends AwsTagProvider {

  def awsTags = stage.awsTags ++ stack.awsTags

  private def removeTrailingSlash(url: String): String =
    if (url.endsWith("/")) url.dropRight(1) else url

  def publicSearchUrl: Option[String] = publicConciergeUrl map { url =>
    s"${removeTrailingSlash(url)}/search?api-key=test&show-fields=all&show-tags=all&q="
  }

  def internalSearchUrl: Option[String] = internalConciergeUrl map { url =>
    s"${removeTrailingSlash(url)}/search?show-fields=all&show-tags=all&q="
  }
}

object Environment {
  implicit val environmentWrites = Json.writes[Environment]
}

case class Panel(
  env: Environment,
  elasticsearchHosts: Seq[String])
