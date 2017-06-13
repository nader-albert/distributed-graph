package na.distributedGraph.commands.corporates

import akka.actor.ActorRef

trait CorporatesPoolCommand

case class Add(business: ActorRef) extends CorporatesPoolCommand
case class Remove(business: ActorRef) extends CorporatesPoolCommand