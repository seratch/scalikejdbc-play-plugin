import sbt._
import Keys._

import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "zentask"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.github.seratch" %% "scalikejdbc-play-plugin" % "0.1.0",
      "com.github.seratch" %% "scalikejdbc"             % "0.6.5"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository"))
    )

}
            
