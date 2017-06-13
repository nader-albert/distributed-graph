package na.distributedGraph.entities.persons
import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import na.distributedGraph.commands.Offer
import na.distributedGraph.commands.persons.{Friend, RelateWith, UnFriend, UnRelateWith}
import na.distributedGraph.events.{Accepted, Fired, FriendRequestAccepted}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class Person(id: Integer) extends Actor with ActorLogging {

    var relatives: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
    var friends: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]

    var employed = false

    import Person._

    override def receive: Receive = {
        employmentRequest orElse relationRequest orElse friendRequest
    }

    private def employmentRequest: Receive = {
        case offer: Offer => employed = true
            log.info("accepted an offer from (%s) with package (%s)".format(sender.path.name, offer.`package`.salary))
            //TODO: If salary less than X, reject the offer
            sender ! Accepted(offer)

        case Fired => employed = false

    }

    private def relationRequest: Receive = {
        case RelateWith(otherPerson) =>
            log.info("(%s) requesting friendship with (%s)".format(self.path.name, otherPerson.path.name))
            implicit val timeout = Timeout(waitTime)
            Await.result (otherPerson ? RelateWith(self), waitTime) match {
                case FriendRequestAccepted =>
                    log.info("friendship request accepted between (%s) and (%s)".format(self.path.name, otherPerson.path.name))
                    friends.+:(otherPerson)
                }

        case UnRelateWith(otherPerson) => otherPerson ! UnRelateWith(self) //TODO: shall we wait for an acknowledgment ?
    }

    private def friendRequest: Receive = {
        case Friend(otherPerson) =>
            log.info("(%s) requesting friendship with (%s)".format(self.path.name, otherPerson.path.name))

            implicit val timeout = Timeout(waitTime)

            Await.result (otherPerson ? Friend(self), waitTime) match {
                case FriendRequestAccepted =>
                    log.info("friendship request accepted between (%s) and (%s)".format(self.path.name, otherPerson.path.name))

                    friends.+:(otherPerson)
            }

        case UnFriend(otherPerson) => otherPerson ! UnFriend(self) //TODO: shall we wait for an acknowledgment ?
    }
}

object Person {

    val waitTime: FiniteDuration = 5 seconds
    def props(id: Integer) = Props(classOf[Person], id)
}

