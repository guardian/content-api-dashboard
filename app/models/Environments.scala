package models

import play.api.Configuration

object Environments {

  def buildEnvironments(config: Configuration): Seq[Environment] = {
    for {
      stage <- Seq(`PROD-VPC`, `CODE-VPC`)
      stack <- Seq(live, preview)
    } yield {
      def cfg(key: String) = config.getString(s"$stage.$stack.$key")
      Environment(
        stage = stage,
        stack = stack,
        fastlyUrl = cfg("fastlyUrl"),
        nightwatchUrl = cfg("nightwatchUrl"),
        pubflowUrl = cfg("pubflowUrl"),
        masheryUrl = cfg("masheryUrl"),
        apiIndexerUrl = cfg("apiIndexerUrl"),
        publicConciergeUrl = cfg("publicConciergeUrl"),
        internalConciergeUrl = cfg("internalConciergeUrl")
      )
    }
  }

}
