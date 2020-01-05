addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.0")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.11.0")

libraryDependencies += "com.h2database" % "h2" % "1.4.200"
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.4.0")