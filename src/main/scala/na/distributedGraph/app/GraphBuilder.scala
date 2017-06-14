package na.distributedGraph.app

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import na.distributedGraph.models.corporates.Add
import na.distributedGraph.entities.businesses.{BusinessGuardian, Employer}
import na.distributedGraph.entities.persons.{Person, Population}
import scala.language.postfixOps


import scala.util.Random

object GraphBuilder extends App {

    val system = ActorSystem("DistributedGraph")

    val config = ConfigFactory load

    val applicationConfig = config getConfig "graph"

    val businessesConfig = applicationConfig getConfig "business_config"
    val personsConfig = applicationConfig getConfig "person_config"

    println("\r\n ******************** Initializing graph data structure ************************** \r\n ")

    val businesses = system.actorOf(BusinessGuardian.props(businessesConfig), name = "businesses-guardian")
    println("\r\n ************************** root corporates node initialised ************************** \r\n" )

    val persons = system.actorOf(Population.props(personsConfig), name = "persons-guardian")
    println("\r\n ************************** Root Persons Node Initialised ************************** \r\n" )


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
