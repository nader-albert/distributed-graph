package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import na.distributedGraph.models.corporates.{Corporates, ListAll}
import na.distributedGraph.models.queries.{Explore, Query}

class Explorer(market: ActorRef) extends Actor with ActorLogging {

    var queryInExecution: Option[Query] = None

    override def receive: Receive = {
        idle orElse executing
    }

    /***
      * TODO: should translate the query into messages
      */
    private def idle: Receive = {
        case Explore(currentQuery) =>
            queryInExecution = Some(currentQuery)
            market ! ListAll
            context become executing
    }

    private def executing: Receive = {
        case Corporates(businesses) =>
            queryInExecution match {
                case Some(query) => print(businesses, query)
                case None => log.error("false state") //TODO throw exception and escalate to supervisor
            }

            queryInExecution = None
            context become idle
    }

    private def print(records: Iterable[ActorRef], query: Query): Unit = {
        log.info("\n\r results for query: (%s) \n\r".format(query))

        records.foreach { record =>
            log.info("\n\r ############################ (%s) ################################ \n\r".format(record.path.name))
        }

    }
}

object Explorer {

    def props(market: ActorRef) = Props(classOf[Explorer], market)
}