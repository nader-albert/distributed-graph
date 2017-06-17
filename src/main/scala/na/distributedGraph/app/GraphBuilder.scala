package na.distributedGraph.app

import com.typesafe.config.ConfigFactory

import akka.pattern.ask
import akka.util.Timeout
import akka.actor.{ActorRef, ActorSystem}

import na.distributedGraph.models.ListAll
import na.distributedGraph.models.explorers.Run
import na.distributedGraph.models.corporates.Hire
import na.distributedGraph.entities.businesses.Market
import na.distributedGraph.entities.persons.Population
import na.distributedGraph.entities.query.ExplorersSquad
import na.distributedGraph.models.queries.{Query, SequenceOf}
import na.distributedGraph.models.dsl.{Corporate, Person, PersonDslParser}
import na.distributedGraph.models.persons.{RequestFriendshipWith, RequestRelationshipWith}

import scala.util.Random
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

object GraphBuilder extends App {

    val system = ActorSystem("graph")

    val waitTime: FiniteDuration = 5 seconds

    val shortsSleepTime: FiniteDuration = 50 seconds

    val sleepTime: FiniteDuration = 100 seconds

    val config = ConfigFactory load

    val applicationConfig = config getConfig "graph"

    val marketConfig = applicationConfig getConfig "business_config"
    val populationConfig = applicationConfig getConfig "population_config"
    val queryConfig = applicationConfig getConfig "query_config"

    println("\r\n ******************** Initializing graph data structure ************************** \r\n ")

    val market = system.actorOf(Market.props(marketConfig), name = "market")
    println("\r\n ************************** Root Corporate node initialised ************************** \r\n" )

    val population = system.actorOf(Population.props(populationConfig), name = "population")
    println("\r\n ************************** Root Population Node Initialised ************************** \r\n" )

    val explorers = system.actorOf(ExplorersSquad.props(queryConfig), name = "insight")
    println("\r\n ************************** Root Query Node Initialised ************************** \r\n" )

    implicit val timeout = Timeout(waitTime)

    val people = bringSquadOf(population)
    val corporates = bringSquadOf(market)

    println("number of people is (%s) and number of corporates is (%s)".format(people.size, corporates.size))

    hire(people, corporates)

    connectFriends(people)

    connectRelatives(people)

    Thread.sleep(shortsSleepTime.toMillis)

    println("Executing queries")

    explorers ! Run(generateQueries)

    Thread.sleep(sleepTime.toMillis)

    println("End program")

    private def bringSquadOf(squad: ActorRef) = {
        Await.result(squad ? ListAll, waitTime) match {
            case SequenceOf(actors) => actors
        }
    }

    private def hire(people: Seq[ActorRef], corporates: Seq[ActorRef]) = {
        people.foreach { person =>
            val randomCorporate = Random.shuffle(corporates).head
            randomCorporate ! Hire(person)
        }
    }

    private def connectFriends(people: Seq[ActorRef]) = {
        few(people)(5).foreach {
            person =>
                var exclude = Seq.empty.+:(person)

                (1 to Random.nextInt(10)).foreach { step => // a person can have a maximum of 10 friends
                    val otherPerson = oneOf(people, exclude)
                    exclude = exclude.+:(otherPerson)

                    person ! RequestFriendshipWith(otherPerson)
                }
        }
    }

    private def connectRelatives(people: Seq[ActorRef]) = {
        few(people)(10).foreach {
            person =>
                var exclude = Seq.empty.+:(person)

                (1 to Random.nextInt(10)).foreach { step => // a person can have a maximum of 10 relatives
                    val otherPerson = oneOf(people, exclude)
                    exclude = exclude.+:(otherPerson)

                    person ! RequestRelationshipWith(otherPerson)
                }
        }
    }

    private def few(actors: Seq[ActorRef], exclusionList: Seq[ActorRef] = Seq.empty)(count: Int = Random.nextInt(actors.size -1)) = {
        Random.shuffle(actors.filterNot(exclusionList.contains).take(count))
    }

    private def oneOf(actors: Seq[ActorRef], exclusionList: Seq[ActorRef] = Seq.empty) = {
        Random.shuffle(actors.filterNot(exclusionList.contains)).head
    }

    private def generateQueries: Seq[Query] = {
        Seq.empty.+:(
            new PersonDslParser {
                find(relativesOf(one(Person("Person-3"))))
            } build
        )/*.+:(
            new PersonDslParser {
                find(every(Person)) who worksAt (Corporate("Corporate-3"))
            } build
        )*//*.+:(
            new PersonDslParser {
                find(relativesOf(every(Person))) who worksAt (Corporate("Corporate-3"))
            } build
        ).+:(
            new PersonDslParser {
                find(every(Person)) who hasFriends(withRelatives(employed))
            } build
        )*/
    }
}


// All relatives of person X
// Relatives of every person who works at business y  ==> RelativesOfAny + WorksAt condition
// all businesses with more than Z
// Every person who has friends with employed relatives ==> RelativesOfFriends + Employed Condition