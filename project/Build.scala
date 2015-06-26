import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play._
import play.sbt.routes.RoutesKeys._
import com.typesafe.sbt.SbtScalariform._

object CapiDashboard extends Build {

  lazy val project = Project(id = "capi-dashboard", base = file("."))
    .enablePlugins(PlayScala)
    .settings(scalariformSettings)
    .settings(
      scalaVersion := "2.11.6",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        ws,
        "com.amazonaws" % "aws-java-sdk-ec2" % "1.10.2"
      ),
      routesGenerator := InjectedRoutesGenerator
    )

}
