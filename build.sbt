ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.mehmetcc"
ThisBuild / organizationName := "mehmetcc"

lazy val ZioVersion       = "2.0.9"
lazy val ZioConfigVersion = "3.0.7"
lazy val QuillVersion     = "4.6.0"
lazy val PostgresVersion  = "42.5.4"
lazy val ZIOHttpVersion   = "0.0.4"
lazy val ZioJsonVersion   = "0.4.2"
lazy val JwtScalaVersion  = "9.2.0"

// Main settings
lazy val root = (project in file("."))
  .settings(
    name := "zio-jwt-starter",
    libraryDependencies ++= Seq(
      "dev.zio"              %% "zio"                 % ZioVersion,
      "dev.zio"              %% "zio-config"          % ZioConfigVersion,
      "dev.zio"              %% "zio-config-typesafe" % ZioConfigVersion,
      "dev.zio"              %% "zio-config-magnolia" % ZioConfigVersion,
      "io.getquill"          %% "quill-zio"           % QuillVersion,
      "io.getquill"          %% "quill-jdbc-zio"      % QuillVersion,
      "org.postgresql"        % "postgresql"          % PostgresVersion,
      "dev.zio"              %% "zio-http"            % ZIOHttpVersion,
      "dev.zio"              %% "zio-json"            % ZioJsonVersion,
      "com.github.jwt-scala" %% "jwt-core"            % JwtScalaVersion,
      "dev.zio"              %% "zio-test"            % ZioVersion % Test
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

// Docker
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerUpdateLatest := true
