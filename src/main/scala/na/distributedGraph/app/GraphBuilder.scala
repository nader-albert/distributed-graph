package na.distributedGraph.app

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import na.distributedGraph.entities.businesses.Market
import na.distributedGraph.entities.persons.Population
import na.distributedGraph.entities.query.ExplorersSquad

import scala.language.postfixOps

object GraphBuilder extends App {

    val system = ActorSystem("DistributedGraph")

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

    //println("\r\n ************************** adding 20 different corporates ************************** \r\n")
    /*(1 to 20).foreach { index =>
        val business = system.actorOf(Employer.props(index), "corporate-" + index)
        businesses ! Add(business)
    }*/

    //println("\r\n ************************** adding 20 different persons ************************** \r\n")

    /*(1 to 20).foreach { index =>
        val person = system.actorOf(Person.props(index), "person-" + index)
        persons ! Add(person)
    }*/

}
