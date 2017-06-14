package na.distributedGraph.entities.businesses

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.models.corporates._

class Market(marketConfig: Config) extends Squad[Employer] with Actor with ActorLogging {

    var businesses: List[ActorRef] = List.empty[ActorRef]

    initialise(marketConfig)

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

        case ListAll => sender ! Corporates(businesses)
     }

    @Override
    def build(corporatesIndex: Int): Unit = {
        val newBusiness = context.actorOf(Employer.props(corporatesIndex), name = "Corporate-" + corporatesIndex)

        newBusiness ! Join

        businesses = newBusiness :: businesses
    }
}

object Market {

    def props(graphConfig: Config) = Props(classOf[Market], graphConfig)
}
