package na.distributedGraph.events

trait RelationshipEvent

case object FriendRequestAccepted extends RelationshipEvent

case object RelationAccepted extends RelationshipEvent
