package na.distributedGraph.entities.persons

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.models.{ListAll, SearchResult}
import na.distributedGraph.models.persons.{Add, Remove}

class Population(populationConfig: Config) extends Squad[Person] with Actor with ActorLogging {

    var persons: List[ActorRef] = List.empty[ActorRef]

    initialise(populationConfig)

    override def receive: Receive = {

        case Add(person) =>
            log.info("adding new person (%s - %s) to the population".format(person, person.path.name))
            persons.::(person)

        case Remove(person) =>
            log.info("person (%s - %s) left the population ".format(person, person.path.name))
            person ! Kill
            persons = persons.filterNot(_ == sender)

        case ListAll => sender ! SearchResult(persons)
    }

    @Override
    def build(personIndex: Int): Unit = {
        val newPerson = context.actorOf(Person.props(personIndex), name = "Person-" + personIndex)

        persons = newPerson :: persons
    }
}

object Population {

    def props(populationConfig: Config) = Props(classOf[Population], populationConfig)
}
