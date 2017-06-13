package na.distributedGraph.entities.businesses

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import na.distributedGraph.commands._
import akka.pattern.ask
import akka.util.Timeout
import na.distributedGraph.commands.corporates.{Fire, Hire, Join}
import na.distributedGraph.events.{Accepted, Fired, Joined, Rejected}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random.{nextDouble, nextString}

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

            implicit val timeout = Timeout(waitTime)

            Await.result (person ? offer , waitTime) match {
                case Accepted => employees.::(sender)

                case Rejected =>
                    log.info("candidate (%s) has rejected the offer(%s)".format(sender, offer))
            }

        case Fire(person) => person ! Fired
            employees = employees.filterNot(_ == person)

    }

    private def preparePackage: Package = Package(nextString(20), nextDouble)
}

object Employer {

    val waitTime: FiniteDuration = 5 seconds

    def props(id: Integer) = Props(classOf[Employer], id)
}

