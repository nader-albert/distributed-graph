package na.distributedGraph.models.queries

import akka.actor.ActorRef

trait Command

case class Add(explorer: ActorRef) extends Command

case class Remove(explorer: ActorRef) extends Command

case class Explore(query: Query) extends Command
