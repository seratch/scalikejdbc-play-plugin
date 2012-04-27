import sbt._
import Keys._

object MyBuild extends Build {

  lazy val root = Project("root", file("."), settings = mainSettings)

  lazy val mainSettings: Seq[Project.Setting[_]] = Defaults.defaultSettings ++ Seq(
    sbtPlugin := false,
    organization := "com.github.seratch",
    name := "scalikejdbc-play-plugin",
    version := "0.1.0",
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

}


