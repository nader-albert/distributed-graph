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

class Person(id: Integer) extends Actor with ActorLogging {

    //TODO: Replace with Sets for uniqueness ... times out check why
    var relatives: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
    var friends: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]

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

            friends.foreach { friend =>
                Await.result (friend ? FindRelatives(isEmployed), waitTime) match {
                    case SequenceOf(friendRelatives) => if (friendRelatives.nonEmpty) matchingFriends = matchingFriends.+:(friend)
                }
            }

            realSender ! SequenceOf(matchingFriends)

        case FindRelatives(isEmployed) =>
            var relativeList = Seq.empty[ActorRef]
            val realSender = sender

            relatives.foreach { relative =>
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

            implicit val timeout = Timeout(waitTime)
            Await.result (otherPerson ? ReceiveRelationshipRequestFrom(self), waitTime) match {
                case FamilyRelationAccepted =>
                    log.info("(%s) became relative with (%s)".format(self.path.name, otherPerson.path.name))
                    relatives = relatives.+:(otherPerson)
                    //relatives + otherPerson
                case _ =>
            }

        case UnRelateWith(otherPerson) =>
            otherPerson ! UnRelateWith(self) //TODO: shall we wait for an acknowledgment ?
            relatives = relatives.filterNot(_ == otherPerson)
    }

    private def requestFriendship: Receive = {
        case RequestFriendshipWith(otherPerson) =>
            log.info("(%s) requesting friendship with (%s)".format(self.path.name, otherPerson.path.name))

            implicit val timeout = Timeout(waitTime)

            Await.result (otherPerson ? ReceiveFriendshipRequestFrom(self), waitTime) match {
                case FriendRequestAccepted =>
                    log.info("(%s) became friend with (%s)".format(self.path.name, otherPerson.path.name))

                    friends = friends.+:(otherPerson)
                    //friends + otherPerson
            }

        case UnFriend(otherPerson) =>
            otherPerson ! UnFriend(self) //TODO: shall we wait for an acknowledgment to make sure the other person is detached?
            friends = friends.filterNot(_ == otherPerson)
    }

    private def receiveRelationship: Receive = {
        case ReceiveRelationshipRequestFrom(otherPerson) =>
            sender ! FriendRequestAccepted

            log.info("(%s) became relative with (%s)".format(self.path.name, otherPerson.path.name))
            relatives = relatives.+:(otherPerson)
            //relatives = relatives + otherPerson
    }

    private def receiveFriendship: Receive = {
        case ReceiveFriendshipRequestFrom(otherPerson) =>
            sender ! FriendRequestAccepted

            log.info("(%s) became friend with (%s)".format(self.path.name, otherPerson.path.name))
            friends = friends.+:(otherPerson)
    }
}

object Person {

    val waitTime: FiniteDuration = 120 seconds //Adding in sets which maintains uniqueness requires quite a bit of extra time
    def props(id: Integer) = Props(classOf[Person], id)
}

