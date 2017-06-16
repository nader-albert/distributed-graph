package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import na.distributedGraph.models.ListAll
import na.distributedGraph.models.persons.FindFriendsWithRelatives
import na.distributedGraph.models.queries._

class Explorer(market: ActorRef, population: ActorRef) extends Actor with ActorLogging {

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

            currentQuery match {
                case HasFriendsWithRelatives(employed) => population

                case relativesOf: RelativesOf => population forward relativesOf

                case friendsWithRelatives: FindFriendsWithRelatives => population forward friendsWithRelatives
            }

            market ! ListAll
            context become executing

    }

    private def executing: Receive = {
        case SearchResult(corporates) =>
            queryInExecution match {
                case Some(query) => print(corporates, query)
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