package na.distributedGraph.entities.persons
import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, Props}
import akka.util.Timeout
import na.distributedGraph.models.Offer
import na.distributedGraph.models.corporates.{Accepted, Fired, Rejected}
import na.distributedGraph.models.persons._
import na.distributedGraph.models.queries._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import java.lang.{Boolean => JBoolean}

class Person(id: Integer) extends Actor with ActorLogging {

    var relatives: ConcurrentHashMap.KeySetView[ActorRef, JBoolean] = ConcurrentHashMap.newKeySet[ActorRef]()
    var friends: ConcurrentHashMap.KeySetView[ActorRef, JBoolean] = ConcurrentHashMap.newKeySet[ActorRef]()

    var employed = false

    var worksAt: Option[ActorPath] = None

    import Person._

    implicit val timeout = Timeout(waitTime)

    override def receive: Receive = {
        query orElse employmentRequest orElse requestRelationship orElse requestFriendship orElse receiveRelationship orElse receiveFriendship
    }

    private def query: Receive = {

        case FindRelativesAndReply(replyTo) => replyTo ! SequenceOf(relatives)

        case FindFriends => sender ! SequenceOf(friends)

        case FindFriendsHavingRelatives(isEmployed) =>
            var matchingFriends = Seq.empty[ActorRef]

            val realSender = sender

            friends foreach { friend =>
                Await.result (friend ? FindRelatives(isEmployed), waitTime) match {
                    case SequenceOf(friendRelatives) => if (friendRelatives.nonEmpty) matchingFriends = matchingFriends.+:(friend)
                }
            }

            realSender ! SequenceOf(matchingFriends)

        case FindRelatives(isEmployed) =>
            var relativeList = Seq.empty[ActorRef]
            val realSender = sender

            relatives foreach { relative =>
                relative ? Employed onSuccess {
                    case ConditionResult(employmentStatus) if employmentStatus == isEmployed => relativeList = relativeList.+:(relative)
                    case _ =>
                }
            }

            realSender ! SequenceOf(relativeList)

        case Employed => sender ! ConditionResult(employed)

        case DoesWorkAt(corporate) => sender ! (if(employed && worksAt.get.name == corporate.name) ConditionResult(true) else ConditionResult(false))
    }

    private def employmentRequest: Receive = {
        case offer: Offer =>
            if(offer.`package`.salary < 8) {
                log.info("rejected an offer from (%s) with package (%s)".format(sender.path.name, offer.`package`.salary))

                sender ! Rejected(offer, "salary package (%s) less than expectation".format(offer.`package`.salary))
            } else {
                log.info("accepted an offer from (%s) with package (%s)".format(sender.path.name, offer.`package`.salary))

                employed = true
                worksAt = Some(sender.path)
                sender ! Accepted(offer)
            }

        case Fired => employed = false
    }

    private def requestRelationship: Receive = {
        case RequestRelationshipWith(otherPerson) =>
            log.info("(%s) requesting family relation with (%s)".format(self.path.name, otherPerson.path.name))

            otherPerson ? ReceiveRelationshipRequestFrom(self) onSuccess {
                case FamilyRelationAccepted =>
                    log.info("(%s) became relative with (%s)".format(self.path.name, otherPerson.path.name))
                    relatives.add(otherPerson)
                case _ =>
            }

        case UnRelateWith(otherPerson) =>
            otherPerson ! UnRelateWith(self) //TODO: shall we wait for an acknowledgment ?
            relatives.remove(otherPerson)
    }

    private def requestFriendship: Receive = {
        case RequestFriendshipWith(otherPerson) =>
            log.info("(%s) requesting friendship with (%s)".format(self.path.name, otherPerson.path.name))

            otherPerson ? ReceiveFriendshipRequestFrom(self) onSuccess {
                case FriendRequestAccepted =>
                    log.info("(%s) became friend with (%s)".format(self.path.name, otherPerson.path.name))

                    friends.add(otherPerson)
            }

        case UnFriend(otherPerson) =>
            otherPerson ! UnFriend(self) //TODO: shall we wait for an acknowledgment to make sure the other person is detached?
            friends.remove(otherPerson)
    }

    private def receiveRelationship: Receive = {
        case ReceiveRelationshipRequestFrom(otherPerson) =>
            relatives.add(otherPerson)

            sender ! FamilyRelationAccepted
    }

    private def receiveFriendship: Receive = {
        case ReceiveFriendshipRequestFrom(otherPerson) =>
            friends.add(otherPerson)

            sender ! FriendRequestAccepted
    }

    implicit def toSeq(set: ConcurrentHashMap.KeySetView[ActorRef, JBoolean]): Seq[ActorRef] = set.asScala.toSeq
}

object Person {
    val waitTime: FiniteDuration = 20 seconds
    def props(id: Integer) = Props(classOf[Person], id)
}

