package na.distributedGraph

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import na.distributedGraph.models.queries.SequenceOf
import na.distributedGraph.entities.persons.Population
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import TestKit._
import na.distributedGraph.models.ListAll

import scala.concurrent.duration._
import scala.language.postfixOps

class PopulationSpec extends TestKit(ActorSystem("PopulationSpec"))
    with Matchers
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll {

    override def afterAll {
        shutdownActorSystem(system)
    }

    "A Population Supervisor " must {
        "build a population size equivalent to the configured number" in {
            val number = 40
            val config = ConfigFactory.parseString("number = %s".format(number))

            val population = system.actorOf(Population.props(config))

            population ! ListAll
            expectMsgPF(2 seconds)({ case SequenceOf(persons) if persons.size-1 == number => } )
        }
    }

    //TODO: cover the rest of scenarios - different PopulationQueries
}
