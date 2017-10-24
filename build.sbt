lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "challenge",
    scalaVersion := "2.12.4",
    version := "1.0",
    libraryDependencies := Seq(
      "com.twitter" %% "finagle-http" % "7.1.0",
      "com.typesafe.play" %% "play-json" % "2.6.6",
      "org.specs2" %% "specs2-core" % "4.0.1" % "test,it"
    ),
    Defaults.itSettings,
    parallelExecution in IntegrationTest := false
  )
