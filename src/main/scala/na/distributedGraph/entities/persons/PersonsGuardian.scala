package na.distributedGraph.entities.persons

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import com.typesafe.config.Config
import na.distributedGraph.commands.persons.{Add, Remove}

class PersonsGuardian(personsConfig: Config) extends Actor with ActorLogging {

    var persons: List[ActorRef] = List.empty[ActorRef]

    //initializePersons()

    override def receive: Receive = {

        case Add(person) =>
            log.info("inviting person (%s - %s) to join the market ".format(person, person.path.name))
            persons.::(person)

        case Remove(person) =>
            log.info("removing person (%s - %s) to join the market ".format(person, person.path.name))
            person ! Kill
            persons = persons.filterNot(_ == sender)
    }

    /*private def initializePersons() = {
        val personsInTheMarket: Int =
            try {
                Integer.parseInt(personsConfig getString "number")
            } catch {
                case ne: NumberFormatException =>
                    log error "configuration problem: invalid squad number "
                    0
                case _:Throwable => 0
            }

        for (corporate <- 1 until personsInTheMarket) {
            persons = context.actorOf(Employer.props(corporate), name = "rover_" + corporate) :: persons
        }
    }*/
}

object PersonsGuardian {

    def props(personsConfig: Config) = Props(classOf[PersonsGuardian], personsConfig)
}
