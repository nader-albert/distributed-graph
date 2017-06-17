package na.distributedGraph.entities.businesses

import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import akka.util.Timeout
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.models.{Join, Leave, ListAll}
import na.distributedGraph.models.corporates._
import na.distributedGraph.models.queries.{FindCorporatesWithEmployeesMoreThan, FindNumberOfEmployees, SearchResult, SequenceOf}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class Market(marketConfig: Config) extends Squad[Employer] with Actor with ActorLogging {

    var businesses: List[ActorRef] = List.empty[ActorRef]

    initialise(marketConfig)

    import Market._

    implicit val timeout = Timeout(waitTime)

    override def receive: Receive = {
        case Add(business) =>
            log.info("inviting business (%s) to join the market ".format(business.path.name))
            business ! Join

        case Remove(business) => business ! Leave //TODO: a business should un-employ all its employees first, before leaving the market

        case Joined =>
            log.info("business (%s) has joined the market ".format(sender.path.name))

            businesses.::(sender)

        case Left =>
            sender ! Kill
            log.info("business (%s) has left the market ".format(sender.path.name))
            businesses = businesses.filterNot(_ == sender)

        case ListAll => sender ! SequenceOf(businesses)

        case FindCorporatesWithEmployeesMoreThan(minimumNumber) =>
            var matchingBusinesses = Seq.empty[ActorRef]

            businesses.foreach { corporate =>
                Await.result(corporate ? FindNumberOfEmployees, Market.waitTime) match {
                    case SearchResult(numberOfEmployees) => if (numberOfEmployees > minimumNumber)
                        matchingBusinesses = matchingBusinesses.:+(corporate)
                }
            }
     }

    override def build(corporatesIndex: Int): Unit = {
        val newBusiness = context.actorOf(Employer.props(corporatesIndex), name = "Corporate-" + corporatesIndex)

        newBusiness ! Join

        businesses = newBusiness :: businesses
    }
}

object Market {

    val waitTime: FiniteDuration = 25 seconds
    def props(graphConfig: Config) = Props(classOf[Market], graphConfig)
}
