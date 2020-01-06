name := """play2-hands-on"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// ↓↓↓↓ここから追加↓↓↓↓
libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.200",
  "org.scalikejdbc" %% "scalikejdbc" % "3.4.0",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.4.0",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.4"
)
// ↑↑↑↑ここまで追加↑↑↑↑

enablePlugins(ScalikejdbcPlugin)

libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-test" % "3.4.0" % Test

javaOptions in Test += "-Dconfig.file=conf/test.conf"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"