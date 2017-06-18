package na.distributedGraph.entities.persons

import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import akka.util.Timeout
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.entities.persons.Person.waitTime
import na.distributedGraph.models.ListAll
import na.distributedGraph.models.dsl._
import na.distributedGraph.models.persons._
import na.distributedGraph.models.queries._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class Population(populationConfig: Config) extends Squad[Person] with Actor with ActorLogging {

    var persons: List[ActorRef] = List.empty[ActorRef]

    initialise(populationConfig)

    implicit val timeout = Timeout(waitTime)

    override def receive: Receive = {
        buildPopulation orElse searchPopulation
    }

    private def buildPopulation: Receive = {
        case Add(person) =>
            log.info("adding new person (%s - %s) to the population".format(person, person.path.name))
            persons.::(person)

        case Remove(person) =>
            log.info("person (%s - %s) left the population ".format(person, person.path.name))
            person ! Kill
            persons = persons.filterNot(_ == sender)
    }

    private def searchPopulation: Receive = {
        case ListAll => sender ! SequenceOf(persons)

        case FindRelativesOf(person) =>
            persons.find(_.path.name == person.name).foreach {
                matchedPerson => matchedPerson ! FindRelativesAndReply(sender)
        }

        case FindRelativesOfWhoWorksAt(corporate) => val realSender = sender
            realSender ! MapOf(findRelativesWhoWorksAt(corporate))

        case FindPersonsWithFriendsHavingRelatives(isEmployed) => val realSender = sender
            realSender ! SequenceOf(findPersonsWithFriendsHavingRelatives(isEmployed))

        case FindPersonsWhoWorkAt(corporate) => val realSender = sender
            realSender ! findPersonsWhoWorksAt(corporate)
    }

    private def findPersonsWhoWorksAt(corporate: Corporate) = {
        var matchingPersons = Seq.empty[ActorRef]

        persons.foreach {
            person =>
                Await.result(person ? DoesWorkAt(corporate), waitTime) match {
                    case ConditionResult(worksAt) => if (worksAt)
                        matchingPersons = matchingPersons.:+(person)
                }
        }

        matchingPersons
    }

    private def findRelativesWhoWorksAt(corporate: Corporate) = {
        var relativesMap = Map.empty[ActorRef, Seq[ActorRef]]

        persons.foreach {
            person =>
                Await.result(person ? DoesWorkAt(corporate), waitTime) match {
                    case ConditionResult(worksAt) => if (worksAt)
                        Await.result(person ? FindRelatives, waitTime) match {
                            case SequenceOf(relatives) =>
                                if (relatives.nonEmpty) relativesMap = relativesMap.updated(person, relatives) else relativesMap
                        }
                }
        }

        relativesMap
    }

    private def findPersonsWithFriendsHavingRelatives(isEmployed: Boolean) = {
        var matchingPersons = Seq.empty[ActorRef]

        persons.foreach { //TODO: This is inefficient, especially with large numbers of persons.. Instead this should be made asynchronous and results should be collected when they are available using a correlation ID
            person => //TODO should be a future
                Await.result(person ? FindFriendsHavingRelatives(isEmployed), waitTime) match {
                    case SequenceOf(matchingFriends) if matchingFriends.nonEmpty => matchingPersons = matchingPersons.+:(person)
                    case _: SequenceOf => matchingPersons = matchingPersons
                }
        }

        matchingPersons
    }

    override def build(personIndex: Int): Unit = {
        val newPerson = context.actorOf(Person.props(personIndex), name = "Person-" + personIndex)

        persons = newPerson :: persons
    }
}

object Population {

    val waitTime: FiniteDuration = 25 seconds

    def props(populationConfig: Config) = Props(classOf[Population], populationConfig)
}
