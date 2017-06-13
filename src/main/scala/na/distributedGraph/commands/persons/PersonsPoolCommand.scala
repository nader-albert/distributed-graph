package na.distributedGraph.commands.persons

import akka.actor.ActorRef

trait PersonsPoolCommand

case class Add(person: ActorRef) extends PersonsPoolCommand
case class Remove(person: ActorRef) extends PersonsPoolCommand