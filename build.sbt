javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions ++= Seq("-feature", "-target:jvm-1.8")
scalaVersion := "2.12.7"

val PlayWsVersion = "2.6.20"

crossScalaVersions := Seq("2.11.12", "2.12.7")
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
publishMavenStyle := true
publishTo := version { (v: String) =>
  val Nexus = "https://t-nx2.performancerocket.com/"
  if (v.trim.endsWith("SNAPSHOT")) Some("ServiceRocket's Snapshots" at Nexus + "content/repositories/snapshots")
  else Some("ServiceRocket's Releases" at Nexus + "content/repositories/releases")
}.value

name := "play-ws-fakes"
version := "0.1.4-SNAPSHOT"
description := "Play WS Test Fakes"
organization := "com.servicerocket"
organizationName := "ServiceRocket"
organizationHomepage := Option(url("http://www.servicerocket.com"))

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws" % PlayWsVersion % Provided