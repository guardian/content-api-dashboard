import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play._
import play.sbt.routes.RoutesKeys._
import com.typesafe.sbt.SbtScalariform._
import com.typesafe.sbt.packager.Keys._
import com.gu.riffraff.artifact._
import RiffRaffArtifact.autoImport._

object ContentApiDashboard extends Build {

  lazy val project = Project(id = "content-api-dashboard", base = file("."))
    .enablePlugins(PlayScala)
    .enablePlugins(RiffRaffArtifact)
    .settings(scalariformSettings)
    .settings(
      scalaVersion := "2.11.6",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        ws,
        "com.amazonaws" % "aws-java-sdk-ec2" % "1.10.2"
      ),
      routesGenerator := InjectedRoutesGenerator,
      riffRaffPackageType := (packageZipTarball in config("universal")).value
    )

}
