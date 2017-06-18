name := "distributed-graph"

version := "1.0"

scalaVersion := "2.11.1"

val akkaV = "2.5.2"

libraryDependencies ++= {
    Seq("com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV)

}
