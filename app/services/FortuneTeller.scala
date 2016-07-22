package services

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.ec2.AmazonEC2AsyncClient
import com.amazonaws.services.ec2.model.{ DescribeInstancesRequest, DescribeInstancesResult, Filter }
import models._

import scala.concurrent.{ Future, Promise }
import scala.collection.JavaConverters._

class FortuneTeller(ec2Client: AmazonEC2AsyncClient) {

  /**
   * Work out whether the stage is currently AARDVARK or ZEBRA
   * by counting the instances with each animal in their Stage tags.
   */
  def whatIsMySpiritAnimal(stagePrefix: StagePrefix, stack: Stack): Future[Animal] = {
    val promise = Promise[Animal]

    val request = new DescribeInstancesRequest()
      .withFilters(stageFilter(stagePrefix.stagePrefix))
      .withFilters(stackFilter(stack.stackName))
      .withFilters(new Filter("instance-state-name").withValues("running"))

    ec2Client.describeInstancesAsync(request, new AsyncHandler[DescribeInstancesRequest, DescribeInstancesResult] {
      override def onSuccess(request: DescribeInstancesRequest, result: DescribeInstancesResult): Unit =
        promise.trySuccess(chooseMorePopularAnimal(result))

      override def onError(exception: Exception): Unit =
        promise.tryFailure(exception)
    })

    promise.future
  }

  private def chooseMorePopularAnimal(describeInstancesResult: DescribeInstancesResult): Animal = {
    val stageTags =
      for {
        reservation <- describeInstancesResult.getReservations.asScala
        instance <- reservation.getInstances.asScala
        tag <- instance.getTags.asScala if tag.getKey == "Stage"
      } yield tag
    val aardvarks = stageTags.count(_.getValue.contains("AARDVARK"))
    val zebras = stageTags.count(_.getValue.contains("ZEBRA"))
    if (aardvarks > zebras) AARDVARK else ZEBRA
  }

  private def toFilters(awsTagProvider: AwsTagProvider): Seq[Filter] =
    awsTagProvider.awsTags.map { case (k, v) => new Filter(s"tag:$k").withValues(v) }

  private def stageFilter(stagePrefix: String): Filter =
    new Filter("tag:Stage").withValues(s"$stagePrefix-AARDVARK", s"$stagePrefix-ZEBRA")

  private def stackFilter(stackName: String): Filter =
    new Filter("tag:Stack").withValues(stackName)

}
