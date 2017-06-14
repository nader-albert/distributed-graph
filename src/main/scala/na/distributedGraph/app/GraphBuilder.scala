package na.distributedGraph.app

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import na.distributedGraph.entities.businesses.Market
import na.distributedGraph.entities.persons.Population
import na.distributedGraph.entities.query.ExplorersSquad
import na.distributedGraph.models.{ListAll, Query, SearchResult}
import na.distributedGraph.models.queries.Explore
import akka.pattern.ask
import akka.util.Timeout
import na.distributedGraph.models.corporates.Hire
import na.distributedGraph.models.persons.{ReceiveFriendshipRequest, RequestRelationship}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scala.util.Random

object GraphBuilder extends App {

    val system = ActorSystem("graph")

    val waitTime: FiniteDuration = 5 seconds

    val sleepTime: FiniteDuration = 30 seconds

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

    Thread.sleep(sleepTime.toMillis)

    println("Executing queries")

    explorers ! Explore(Query("TEST Query"))

    Thread.sleep(sleepTime.toMillis)

    println("End program")

    private def bringSquadOf(squad: ActorRef) = {
        Await.result(squad ? ListAll, waitTime) match {
            case SearchResult(actors) => actors
        }
    }

    private def hire(people: Iterable[ActorRef], corporates: Iterable[ActorRef]) = {
        few(corporates).foreach {
            corporate =>
                corporate ! Hire(oneOf(people))
                corporate ! Hire(oneOf(people))
                corporate ! Hire(oneOf(people))
        }

        few(corporates).foreach {
            corporate =>
                corporate ! Hire(oneOf(people))
                corporate ! Hire(oneOf(people))
                corporate ! Hire(oneOf(people))
                corporate ! Hire(oneOf(people))
        }

        few(corporates).foreach {
            corporate =>
                corporate ! Hire(oneOf(people))
                corporate ! Hire(oneOf(people))
        }
    }

    private def connectFriends(people: Iterable[ActorRef]) = {
        few(people).foreach {
            person =>
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
        }

        few(people).foreach {
            person =>
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
        }

        few(people).foreach {
            person =>
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
                person ! ReceiveFriendshipRequest(oneOf(people, Some(person)))
        }
    }

    private def connectRelatives(people: Iterable[ActorRef]) = {
        few(people).foreach {
            person =>
                person ! RequestRelationship(oneOf(people, Some(person)))
                person ! RequestRelationship(oneOf(people, Some(person)))
                person ! RequestRelationship(oneOf(people, Some(person)))
        }

        few(people).foreach {
            person =>
                person ! RequestRelationship(oneOf(people, Some(person)))
                person ! RequestRelationship(oneOf(people, Some(person)))
                person ! RequestRelationship(oneOf(people, Some(person)))
                person ! RequestRelationship(oneOf(people, Some(person)))
        }

        few(people).foreach {
            person =>
                person ! RequestRelationship(oneOf(people, Some(person)))
                person ! RequestRelationship(oneOf(people, Some(person)))
        }
    }

    private def few(actors: Iterable[ActorRef]) = Random.shuffle(actors).take(Random.nextInt(actors.size -1))

    private def oneOf(actors: Iterable[ActorRef], exceptMe: Option[ActorRef] = None) = {
        Random.shuffle(exceptMe.fold(actors)(me => actors.filterNot(_ == me))).head
    }

    private def generateQueries() = {

    }
}
