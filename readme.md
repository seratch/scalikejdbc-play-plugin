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
  "com.github.seratch" %% "scalikejdbc"             % "1.0.0",
  "com.github.seratch" %% "scalikejdbc-play-plugin" % "1.0.0"
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
db.default.driver="org.h2.Driver"
db.default.url="jdbc:h2:mem:play"
db.default.user="sa"
db.default.password="sa"

# original properties
#db.default.poolInitialSize=10
#db.default.poolMaxSize=20
#db.default.poolValidationQuery="select 1 from dual"

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

  private val simple = (rs: WrappedResultSet) => Project(
    rs.long("id"), 
    rs.string("folder"), 
    rs.string("name")
  )

  def findById(id: Long): Option[Project] = {
    DB readOnly { implicit session =>
      SQL("select * from project where id = ?").bind(id).map(simple).single.apply()
    }
  }

...
```

## Generating models

See also:

https://github.com/seratch/scalikejdbc-mapper-generator


