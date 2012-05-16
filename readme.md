# ScalikeJDBC Play Plugin

## ScalikeJDBC

A thin JDBC wrapper library.

https://github.com/seratch/scalikejdbc

## Play 2.0 Scala

ScalikeJDBC works with Play20 seamlessly.

http://www.playframework.org/documentation/2.0/ScalaHome

## How to use?

See Zentasks example in detail.

https://github.com/seratch/scalikejdbc-play-plugin/tree/master/test/zentasks

### project/Build.scala

```scala
val appDependencies = Seq(
  "com.github.seratch" %% "scalikejdbc"             % "1.1.1",
  "com.github.seratch" %% "scalikejdbc-play-plugin" % "1.1.1"
)

val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository"))
)
```

### conf/application.conf

This plugin uses the default Database configuration.

```
# Database configuration
# ~~~~~ 
# You can declare as many datasources as you want.
# By convention, the default datasource is named `default`
db.default.driver=org.h2.Driver
db.default.url="jdbc:h2:mem:play"
db.default.user="sa"
db.default.password="sa"

# ScalikeJDBC original configuration

#db.default.poolInitialSize=10
#db.default.poolMaxSize=10
#db.default.poolValidationQuery=

db.global.loggingSQLAndTime.enabled=true
db.global.loggingSQLAndTime.logLevel=debug
db.global.loggingSQLAndTime.warningEnabled=true
db.global.loggingSQLAndTime.warningThresholdMillis=1000
db.global.loggingSQLAndTime.warningLogLevel=warn

# You can disable the default DB plugin
dbplugin=disabled
evolutionplugin=disabled
```

### conf/play.plugins

```
777:scalikejdbc.PlayPlugin
```

### app/models/Project.scala

```scala
import scalikejdbc._

case class Project(id: Long, folder: String, name: String)

object Project {

  private val * = (rs: WrappedResultSet) => Project(
    rs.long("id"), 
    rs.string("folder"), 
    rs.string("name")
  )

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[Project] = {
    SQL("select * from project where id = ?").bind(id).map(*).single.apply()
  }

...
```

## Generating models

See also:

https://github.com/seratch/scalikejdbc-mapper-generator


