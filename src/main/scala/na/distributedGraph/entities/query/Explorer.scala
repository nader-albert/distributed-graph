package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import na.distributedGraph.models.explorers.Explore
import na.distributedGraph.models.queries._

class Explorer(market: ActorRef, population: ActorRef) extends Actor with ActorLogging {

    var queryInExecution: Option[Query] = None

    override def receive: Receive = {
        idle orElse executing
    }

    private def idle: Receive = {
        case Explore(currentQuery) =>
            currentQuery match {
                case marketQuery: MarketQuery => market forward marketQuery
                case personQuery: PopulationQuery => population forward personQuery
                case _ =>
            }

            queryInExecution = Some(currentQuery)
            context become executing
    }

    private def executing: Receive = {
        case SequenceOf(elements) =>
            queryInExecution match {
                case Some(query) => print(elements, query)
                case None => log.error("false state, a results message has been received without a corresponding query") //TODO throw exception and escalate to supervisor
            }

            queryInExecution = None
            context become idle
    }

    private def print(records: Iterable[ActorRef], query: Query): Unit = {
        log.info("\n\r results for query: (%s) \n\r [ ************************ ".format(query))

        records.foreach { record =>
            println("\t\t(%s)".format(record.path.name))
        }
        println(" ************************ ]")

    }
}

object Explorer {

    def props(market: ActorRef) = Props(classOf[Explorer], market)
}