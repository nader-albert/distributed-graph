package na.distributedGraph.entities.persons

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.businesses.Employer
import na.distributedGraph.models.persons.{Add, Remove}

class PersonsGuardian(personsConfig: Config) extends Actor with ActorLogging {

    var persons: List[ActorRef] = List.empty[ActorRef]

    initializePersons()

    override def receive: Receive = {

        case Add(person) =>
            log.info("inviting person (%s - %s) to join the market ".format(person, person.path.name))
            persons.::(person)

        case Remove(person) =>
            log.info("removing person (%s - %s) to join the market ".format(person, person.path.name))
            person ! Kill
            persons = persons.filterNot(_ == sender)
    }

    private def initializePersons() = {
        val personsInThePopulation: Int =
            try {
                Integer.parseInt(personsConfig getString "number")
            } catch {
                case ne: NumberFormatException =>
                    log error "configuration problem: invalid squad number "
                    0
                case _:Throwable => 0
            }

        log.info ("\r\n ************************** People Guardian adding (%s) different persons ************************** \r\n".format(personsInThePopulation))

        for (personIndex <- 1 until personsInThePopulation) add(personIndex)
    }

    private def add(personIndex: Int) = {
        val newPerson = context.actorOf(Employer.props(personIndex), name = "Person-" + personIndex)

        persons = newPerson :: persons
    }
}

object PersonsGuardian {

    def props(personsConfig: Config) = Props(classOf[PersonsGuardian], personsConfig)
}
