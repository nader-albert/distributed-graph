name := "distributed-graph"

version := "1.0"

scalaVersion := "2.12.2"

val akkaV = "2.5.2"

libraryDependencies ++= {
    Seq("com.typesafe.akka" %% "akka-actor" % akkaV)
}
