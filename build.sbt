name := "icnsgen"

version := "0.0.0-SNAPSHOT"

organization := "de.choffmeister"

scalaVersion := "2.10.4"

scalacOptions := Seq("-encoding", "utf8")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.11" % "test"
)
