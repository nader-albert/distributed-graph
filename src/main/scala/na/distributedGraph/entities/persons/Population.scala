package na.distributedGraph.entities.persons

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.entities.businesses.Employer
import na.distributedGraph.models.{ListAll, SearchResult}
import na.distributedGraph.models.persons.{Add, Remove}

class Population(populationConfig: Config) extends Squad[Person] with Actor with ActorLogging {

    var persons: List[ActorRef] = List.empty[ActorRef]

    initialise(populationConfig)

    override def receive: Receive = {

        case Add(person) =>
            log.info("inviting person (%s - %s) to join the market ".format(person, person.path.name))
            persons.::(person)

        case Remove(person) =>
            log.info("removing person (%s - %s) to join the market ".format(person, person.path.name))
            person ! Kill
            persons = persons.filterNot(_ == sender)

        case ListAll => sender ! SearchResult(persons)

    }

    @Override
    def build(personIndex: Int): Unit = {
        val newPerson = context.actorOf(Employer.props(personIndex), name = "Person-" + personIndex)

        persons = newPerson :: persons
    }
}

object Population {

    def props(populationConfig: Config) = Props(classOf[Population], populationConfig)
}
