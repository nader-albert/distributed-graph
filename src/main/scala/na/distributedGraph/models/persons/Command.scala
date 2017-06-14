package na.distributedGraph.models.persons

import akka.actor.ActorRef

trait Command

case class Friend(friend: ActorRef) extends Command

case class UnFriend(friend: ActorRef) extends Command

case class RelateWith(relative: ActorRef) extends Command

case class UnRelateWith(relative: ActorRef) extends Command

case class Add(person: ActorRef) extends Command

case class Remove(person: ActorRef) extends Command
