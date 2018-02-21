val ScalatraVersion = "2.6.2"

organization := "com.test"

name := "inference-server"

version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.1"
resolvers += Classpaths.typesafeReleases
mainClass in assembly := Some("com.test.app.JettyLauncher")

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.8.v20171121" % "container;compile",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s"   %% "json4s-jackson" % "3.5.0",
  "ml.combust.mleap" %% "mleap-runtime" % "0.9.0",
)
assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", xs@_*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
enablePlugins(JettyPlugin)

containerPort in Jetty := 8090
