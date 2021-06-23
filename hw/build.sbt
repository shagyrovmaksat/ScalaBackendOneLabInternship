name := "test1"

version := "0.1"

scalaVersion := "2.12.10"

lazy val akkaVersion     = "2.5.23"
lazy val akkaHttpVersion = "10.1.9"
lazy val json4sVersion   = "3.5.4"
lazy val elastic4sVersion = "7.0.1"

libraryDependencies ++= Seq (
  "com.typesafe.akka"      %% "akka-actor"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-slf4j"            % akkaVersion,
  "com.typesafe.akka"      %% "akka-http"             % akkaHttpVersion,
  "de.heikoseeberger"      %% "akka-http-json4s"      % "1.20.1",
  "com.typesafe.akka"      %% "akka-stream"           % akkaVersion,

  "org.json4s"             %% "json4s-core"           % json4sVersion,
  "org.json4s"             %% "json4s-jackson"        % json4sVersion,
  "org.json4s"             %% "json4s-native"         % json4sVersion,

  "com.sksamuel.elastic4s" %% "elastic4s-core"        % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json-json4s" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-client-akka" % elastic4sVersion,
)