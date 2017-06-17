package na.distributedGraph.models.explorers

import akka.actor.ActorRef
import na.distributedGraph.models.queries.Query

trait Command

case class Add(explorer: ActorRef) extends Command

case class Remove(explorer: ActorRef) extends Command

case class Run(queries: Seq[Query]) extends Command

case class Explore(query: Query) extends Command
