package na.distributedGraph.models.persons

import akka.actor.ActorRef
import na.distributedGraph.models.queries.Corporate

trait Command

case class RequestFriendshipWith(friend: ActorRef) extends Command

case class ReceiveFriendshipRequestFrom(friend: ActorRef) extends Command

case class UnFriend(friend: ActorRef) extends Command

case class RequestRelationshipWith(relative: ActorRef) extends Command

case class ReceiveRelationshipRequestFrom(relative: ActorRef) extends Command

case class UnRelateWith(relative: ActorRef) extends Command

case class Add(person: ActorRef) extends Command

case class Remove(person: ActorRef) extends Command

case class FindFriends(employed: Boolean) extends Command

case class FindFriendsWithRelatives(employed: Boolean) extends Command

case object FindFriends extends Command

case class FindRelatives(employed: Boolean) extends Command

case object FindRelatives extends Command

case object Employed extends Command

case class RelativesOfWorksAt(corporate: Corporate) extends Command

case class WorksAt(corporate: Corporate) extends Command

