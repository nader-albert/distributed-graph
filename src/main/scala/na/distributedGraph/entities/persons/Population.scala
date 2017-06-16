package na.distributedGraph.entities.persons

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import akka.util.Timeout
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.entities.persons.Person.waitTime
import na.distributedGraph.models.ListAll
import na.distributedGraph.models.persons._
import na.distributedGraph.models.queries.{MapOf, RelativesOf, SearchResult, SequenceOf}
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration

class Population(populationConfig: Config) extends Squad[Person] with Actor with ActorLogging {

    var persons: List[ActorRef] = List.empty[ActorRef]

    initialise(populationConfig)

    implicit val timeout = Timeout(waitTime)

    override def receive: Receive = {

        case Add(person) =>
            log.info("adding new person (%s - %s) to the population".format(person, person.path.name))
            persons.::(person)

        case Remove(person) =>
            log.info("person (%s - %s) left the population ".format(person, person.path.name))
            person ! Kill
            persons = persons.filterNot(_ == sender)

        case ListAll => sender ! SequenceOf(persons)

        case RelativesOf(person) => persons.find(_ == person).foreach { matchedPerson =>
            Await.result(matchedPerson ? FindRelatives, waitTime) match {
                case relatives: SequenceOf => sender forward relatives
            }
        }

        case RelativesOfWorksAt(corporate) =>
            var relativesMap = Map.empty[ActorRef, Seq[ActorRef]]

            persons.foreach {
                person =>
                    Await.result(person ? WorksAt(corporate), waitTime) match {
                        case SearchResult(worksAt) => if (worksAt) Await.result(person ? FindRelatives, waitTime) match {

                            case SequenceOf(relatives) => if (relatives.nonEmpty) relativesMap = relativesMap.updated(person, relatives)
                        }
                }

            sender ! MapOf(relativesMap)
        }

        case FindFriendsWithRelatives(isEmployed) =>
            var matchingPersons = Seq.empty[ActorRef]

            persons.foreach {
                person =>
                    Await.result(person ? FindFriendsWithRelatives(isEmployed), waitTime) match {
                        case SequenceOf(matchingFriends) if matchingFriends.nonEmpty => matchingPersons = matchingPersons.+:(person)
                    }
            }

            sender ! SequenceOf(matchingPersons)
    }

    @Override
    def build(personIndex: Int): Unit = {
        val newPerson = context.actorOf(Person.props(personIndex), name = "Person-" + personIndex)

        persons = newPerson :: persons
    }
}

object Population {

    val waitTime: FiniteDuration = 25 seconds

    def props(populationConfig: Config) = Props(classOf[Population], populationConfig)
}
