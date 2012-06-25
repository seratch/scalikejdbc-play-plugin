import sbt._
import Keys._

object MyBuild extends Build {

  lazy val root = Project("root", file("."), settings = mainSettings)

  lazy val mainSettings: Seq[Project.Setting[_]] = Defaults.defaultSettings ++ Seq(
    sbtPlugin := false,
    organization := "com.github.seratch",
    name := "scalikejdbc-play-plugin",
    version := "1.3.2",
    scalaVersion := "2.9.1",
    externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository")),
    resolvers ++= Seq(
      "sonatype" at "https://oss.sonatype.org/content/repositories/releases",
      "typesafe" at "http://repo.typesafe.com/typesafe/releases"
    ),
    libraryDependencies <++= (scalaVersion) { scalaVersion =>
      Seq(
        "com.github.seratch" %% "scalikejdbc" % "1.3.2",
        "play" %% "play" % "2.0.1" % "provided",
        "play" %% "play-test" % "2.0.1" % "test"
      )
    },
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
        else Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    pomExtra := (
      <url>http://seratch.github.com/scalikejdbc-play-plugin</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:seratch/scalikejdbc-play-plugin.git</url>
        <connection>scm:git:git@github.com:seratch/scalikejdbc-play-plugin.git</connection>
      </scm>
      <developers>
        <developer>
          <id>seratch</id>
          <name>Kazuhuiro Sera</name>
          <url>http://seratch.net/</url>
        </developer>
      </developers>
    ),
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )

}


