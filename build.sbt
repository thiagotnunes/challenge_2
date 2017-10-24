name := "challenge"

scalaVersion := "2.12.4"
version := "1.0"

libraryDependencies := Seq(
  "com.twitter" %% "finagle-http" % "7.1.0",
  "org.specs2" %% "specs2-core" % "4.0.1" % "test"
)
