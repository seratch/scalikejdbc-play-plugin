import testgen.TestgenKeys._

scalaVersion := "2.9.1"

crossScalaVersions := Seq("2.9.1")

externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository"))

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies <++= (scalaVersion) { scalaVersion =>
  Seq(
    "com.github.seratch" %% "scalikejdbc" % "[0.6,)",
    "play" %% "play" % "[2,)"
  )
}

// http://ls.implicit.ly/

seq(lsSettings :_*)

// https://github.com/typesafehub/sbtscalariform

seq(scalariformSettings: _*)

// https://github.com/seratch/testgen-sbt

seq(testgenSettings: _*)

testgenEncoding in Compile := "UTF-8"

testgenTestTemplate in Compile := "scalatest.FlatSpec"

testgenScalaTestMatchers in Compile := "ShouldMatchers"

testgenLineBreak in Compile := "LF"


