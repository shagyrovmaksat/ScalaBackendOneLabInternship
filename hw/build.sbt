name := "test1"

version := "0.1"

scalaVersion := "2.12.10"

lazy val akkaVersion = "2.5.23"

libraryDependencies ++= Seq (
  "com.typesafe.akka"            %% "akka-actor"    % akkaVersion,
  "com.typesafe.akka"            %% "akka-slf4j"    % akkaVersion,
)