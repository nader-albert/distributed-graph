package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import na.distributedGraph.models.explorers.Explore
import na.distributedGraph.models.queries._

class Explorer(market: ActorRef, population: ActorRef) extends Actor with ActorLogging with Stash{

    var queryInExecution: Option[Query] = None

    override def receive: Receive = {
        idle orElse executing
    }

    private def idle: Receive = {
        case Explore(currentQuery) =>
            currentQuery match {
                case marketQuery: MarketQuery => market ! marketQuery
                case personQuery: PopulationQuery => population ! personQuery
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
            unstashAll()
            context become idle

        case MapOf(actors) =>
            queryInExecution match {
                case Some(query) => print(actors, query)
                case None => log.error("false state, a results message has been received without a corresponding query") //TODO throw exception and escalate to supervisor
            }
            queryInExecution = None
            unstashAll()
            context become idle

        case Explore => stash()
    }

    private def print(records: Seq[ActorRef], query: Query): Unit = {
        log.info(("\n\r results for query: (%s) (%s records) \n\r " +
            "[ *************************************************************** \n\r " +
            "\t\t %s \n\r ****************************************************]")
            .format(query, records.size, records.map(_.path.name)
                .fold("")((acc, record) => acc + "\n\r Record: (-- %s--)".format(record))))

        //records.foreach { record => println("\t\t Record[%s]: (-- %s --)".format(query, record.path.name)) }

        //log.info("************************ ]")
    }

    private def print(records: Map[ActorRef, Seq[ActorRef]], query: Query): Unit = {
        log.info(("\n\r results for query: (%s) (%s) records) \n\r " +
            "[ ************************ ").format(query, records.size))

        records.foreach { record => println("\t\t Record: (%s) :=> (%s)"
            .format(record._1.path.name, record._2.map(_.path.name).fold("")((acc, actorName) => acc + "/" + actorName)))
        }
        println(" ************************ ]")
    }
}

object Explorer {

    def props(market: ActorRef, population: ActorRef) = Props(classOf[Explorer], market, population)
}