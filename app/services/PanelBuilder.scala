package services

import models.{ Elasticsearch, Panel, Environment }

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PanelBuilder(eC2InstanceFinder: EC2InstanceFinder) {

  def makePanel(environment: Environment): Future[Panel] = {
    val elasticsearchInstanceAwsTags = environment.awsTags ++ Elasticsearch.awsTags
    eC2InstanceFinder.findInstanceHostnames(elasticsearchInstanceAwsTags) map { elasticsearchHostnames =>
      Panel(environment, elasticsearchHostnames)
    }
  }

}
