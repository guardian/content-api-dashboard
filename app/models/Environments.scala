package models

import play.api.Configuration

object Environments {

  def buildEnvironments(config: Configuration): Seq[Environment] = {
    for {
      stage <- Seq(`PROD`, `CODE`)
      stack <- Seq(live, preview)
    } yield {
      def cfg(key: String) = config.getString(s"$stage.$stack.$key")
      Environment(
        stage = stage,
        stack = stack,
        nightwatchUrl = cfg("nightwatchUrl"),
        pubflowUrl = cfg("pubflowUrl"),
        apiIndexerUrl = cfg("apiIndexerUrl"),
        publicConciergeUrl = cfg("publicConciergeUrl"),
        internalConciergeUrl = cfg("internalConciergeUrl")
      )
    }
  }

}
