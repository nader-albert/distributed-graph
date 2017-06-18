package na.distributedGraph.entities.businesses

import akka.util.Timeout
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import na.distributedGraph.models._
import na.distributedGraph.models.corporates._
import na.distributedGraph.models.queries.{FindNumberOfEmployees, SearchResult}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random.{nextInt, nextString}

class Employer(id: Integer) extends Actor with ActorLogging {

    var employees: List[ActorRef] = List.empty[ActorRef]

    import Employer._

    override def receive: Receive = {
        case Join =>
            sender ! Joined
            context become inAction
    }

    private def inAction: Receive = {
        case Hire(person) =>

            val offer = Offer(preparePackage)

            log.info("(%s) hiring (%s) with offer salary (%s)".format(self.path.name, person.path.name, offer.`package`.salary))

            implicit val timeout = Timeout(waitTime)

            person ! offer

            //TODO: hiring process should be made asynchronous
            /*Await.result (person ? offer , waitTime) match {
                case Accepted => employees.::(sender)

                case Rejected(submitedOffer, reason) =>
                    log.info("candidate (%s) has rejected the offer(%s) due to (%s)".format(submitedOffer, offer, reason))
            }*/

        case Fire(person) => person ! Fired
            employees = employees.filterNot(_ == person)

        case Accepted(submittedOffer) =>
            log.info("candidate (%s) has accepted the offer(%s)".format(sender.path.name, submittedOffer))

            employees = employees.::(sender)

        case Rejected(submittedOffer, reason) =>
            log.info("candidate (%s) has rejected the offer(%s) due to (%s)".format(sender.path.name, submittedOffer, reason))

        case FindNumberOfEmployees => sender ! SearchResult(employees.size-1)
    }

    private def preparePackage: Package = Package(nextString(20), nextInt(20))
}

object Employer {

    val waitTime: FiniteDuration = 5 seconds

    def props(id: Integer) = Props(classOf[Employer], id)
}

