package models

import play.api.libs.json.{ Json, JsString, JsValue, Writes }

trait AwsTagProvider {
  def awsTags: Seq[(String, String)]
}

sealed trait StagePrefix extends AwsTagProvider {
  def stagePrefix: String

  def awsTags = Seq(
    ("Stage", stagePrefix)
  )

  def toStageName(animal: Animal): String = s"$stagePrefix-$animal"
}
object StagePrefix {
  implicit val writes = new Writes[StagePrefix] {
    override def writes(stage: StagePrefix): JsValue = JsString(stage.stagePrefix)
  }
}
case object `PROD` extends StagePrefix { val stagePrefix = "PROD" }
case object `CODE` extends StagePrefix { val stagePrefix = "CODE" }

sealed trait Animal {
  def imageUrl: String
}
case object AARDVARK extends Animal { def imageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/12/Aardvark2_(PSF).png" }
case object ZEBRA extends Animal { def imageUrl = "https://pixabay.com/static/uploads/photo/2015/10/04/16/40/silhouette-971334_960_720.png" }

sealed trait Stack extends AwsTagProvider {
  def stackName: String

  def awsTags = Seq(
    ("Stack", s"%5E$stackName$$")
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
    ("App", s"%5E$appName$$")
  )
}
case object Concierge extends App { val appName = "concierge" }
case object Elasticsearch extends App { val appName = "elasticsearch" }
case object Porter extends App { val appName = "porter" }

object App {
  val apps = Seq(Concierge, Elasticsearch, Porter)
}

case class Environment(
    stagePrefix: StagePrefix,
    stack: Stack,
    nightwatchUrl: Option[String],
    pubflowUrl: Option[String],
    bonoboUrl: Option[String],
    publicConciergeUrl: Option[String],
    internalConciergeUrl: Option[String]) extends AwsTagProvider {

  def awsTags = stagePrefix.awsTags ++ stack.awsTags

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

case class Panel(env: Environment, animal: Animal)
