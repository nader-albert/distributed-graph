package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.persons.PersonsGuardian

class ExplorersSquad(queryConfig: Config) extends Actor with ActorLogging {

    var explorers: List[ActorRef] = List.empty[ActorRef]

    initializeSquad()

    /***
      * receives a query, pick up a random Explorer and sends it the message to ditribute the load
      * */
    override def receive: Receive = ???

    private def initializeSquad() = {
        val explorersInAction: Int =
            try {
                Integer.parseInt(queryConfig getString "number")
            } catch {
                case ne: NumberFormatException =>
                    log error "configuration problem: invalid number "
                    0
                case _:Throwable => 0
            }

        for (explorerIndex <- 1 until explorersInAction) {
            explorers = context.actorOf(Explorer.props(), name = "Explorer-" + explorerIndex) :: explorers
        }
    }
}

object ExplorersSquad {

    def props(queryConfig: Config) = Props(classOf[PersonsGuardian], queryConfig)
}
