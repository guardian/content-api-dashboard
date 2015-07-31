import sbt._
import Keys._
import play.sbt._
import play.sbt.Play.autoImport._
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
      scalaVersion := "2.11.7",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        ws,
        "com.amazonaws" % "aws-java-sdk-ec2" % "1.10.2",
        "com.gu" %% "play-googleauth" % "0.3.0"
      ),
      routesGenerator := InjectedRoutesGenerator,
      riffRaffPackageType := (packageZipTarball in config("universal")).value
    )

}
