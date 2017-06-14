package na.distributedGraph.models

import akka.actor.ActorRef

trait Message

case class SearchResult(actorList: Iterable[ActorRef])

case class Query private(search: String) {

    override def toString: String = {
        "QUERY"
    }
}

object Query {

    def apply: Query = new Query("")
}
