package na.distributedGraph.models.persons

import akka.actor.ActorRef

trait Command

case class RequestFriendshipWith(friend: ActorRef) extends Command

case class ReceiveFriendshipRequestFrom(friend: ActorRef) extends Command

case class UnFriend(friend: ActorRef) extends Command

case class RequestRelationshipWith(relative: ActorRef) extends Command

case class ReceiveRelationshipRequestFrom(relative: ActorRef) extends Command

case class UnRelateWith(relative: ActorRef) extends Command

case class Add(person: ActorRef) extends Command

case class Remove(person: ActorRef) extends Command
