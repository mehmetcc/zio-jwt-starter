ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.mehmetcc"
ThisBuild / organizationName := "example"

lazy val ZioVersion = "2.0.8"

lazy val root = (project in file("."))
  .settings(
    name := "zio-jwt-starter",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"      % ZioVersion,
      "dev.zio" %% "zio-test" % ZioVersion % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
