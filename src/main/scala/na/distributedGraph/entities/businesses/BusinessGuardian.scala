package na.distributedGraph.entities.businesses

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import com.typesafe.config.Config
import na.distributedGraph.models.corporates._

class BusinessGuardian(graphConfig: Config) extends Actor with ActorLogging {

    var businesses: List[ActorRef] = List.empty[ActorRef]

    initializeBusinesses()

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
            //val loader = Await.result(context.actorSelection(self.path/"*")
     }

    private def initializeBusinesses() = {
        val corporatesInMarket: Int =
            try {
                Integer.parseInt(graphConfig getString "number")
            } catch {
                case ne: NumberFormatException =>
                    log error "configuration problem: invalid number "
                    0
                case _:Throwable => 0
            }

        log.info ("\r\n ************************** Establishing (%s) different corporates ************************** \r\n".format(corporatesInMarket))

        for (corporateId <- 1 until corporatesInMarket) establish(corporateId)
    }

    private def establish(corporatesIndex: Int) = {
        val newBusiness = context.actorOf(Employer.props(corporatesIndex), name = "Corporate-" + corporatesIndex)

        newBusiness ! Join

        businesses = newBusiness :: businesses
    }
}

object BusinessGuardian {

    def props(graphConfig: Config) = Props(classOf[BusinessGuardian], graphConfig)
}
