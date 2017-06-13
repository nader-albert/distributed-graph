package na.distributedGraph.commands.persons

import akka.actor.ActorRef

trait RelationshipCommand

case class Friend(friend: ActorRef) extends RelationshipCommand

case class UnFriend(friend: ActorRef) extends RelationshipCommand

case class RelateWith(relative: ActorRef) extends RelationshipCommand

case class UnRelateWith(relative: ActorRef) extends RelationshipCommand

