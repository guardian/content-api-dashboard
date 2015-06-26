package services

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2AsyncClient
import com.amazonaws.services.ec2.model.{ DescribeInstancesResult, Filter, DescribeInstancesRequest }
import play.api.Configuration

import scala.concurrent.{ Promise, Future }
import scala.collection.JavaConverters._

class EC2InstanceFinder(config: Configuration) {

  private val client = {
    val region = Regions.fromName(config.getString("aws.region") getOrElse "eu-west-1")
    val client = new AmazonEC2AsyncClient()
    client.configureRegion(region)
    client
  }

  def findInstanceHostnames(tags: Seq[(String, String)]): Future[Seq[String]] = {
    val filters = tags.map {
      case (tagName, value) =>
        new Filter(s"tag:$tagName", java.util.Arrays.asList(s"$value"))
    }.asJava

    val request = new DescribeInstancesRequest()
    request.setFilters(filters)

    val p = Promise[Seq[String]]()
    client.describeInstancesAsync(request, new AsyncHandler[DescribeInstancesRequest, DescribeInstancesResult] {

      override def onError(exception: Exception): Unit = p.tryFailure(exception)

      override def onSuccess(request: DescribeInstancesRequest, result: DescribeInstancesResult): Unit = {
        val hostnames = for {
          reservation <- result.getReservations.asScala
          instance <- reservation.getInstances.asScala
          hostname <- Option(instance.getPublicDnsName)
        } yield hostname
        p.trySuccess(hostnames)
      }

    })
    p.future
  }

}
