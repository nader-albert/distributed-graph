package na.distributedGraph.models.corporates

import akka.actor.ActorRef

trait Message

case class Corporates(corporateList: Iterable[ActorRef])