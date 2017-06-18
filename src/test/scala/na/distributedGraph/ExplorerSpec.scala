package na.distributedGraph

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import na.distributedGraph.entities.query.Explorer
import na.distributedGraph.models.explorers.Explore
import na.distributedGraph.models.queries.{FindNumberOfEmployees, FindPersonsWithFriendsHavingRelatives, SequenceOf}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.postfixOps
import TestKit._

class ExplorerSpec() extends TestKit(ActorSystem("ExplorerSpec"))
    with Matchers
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll {

    override def afterAll {
        shutdownActorSystem(system)
    }

    "An Explorer " must {
        "forward a population query to the population actor" in {
            val population = TestProbe()
            val market = TestProbe()

            val explorer = system.actorOf(Explorer.props(market.ref, population.ref))

            explorer ! Explore(FindPersonsWithFriendsHavingRelatives(true))
            population.expectMsg(FindPersonsWithFriendsHavingRelatives(true))
        }
    }

    "An Explorer " must {
        "forward a market query to the market actor" in {
            val population = TestProbe()
            val market = TestProbe()

            val explorer = system.actorOf(Explorer.props(market.ref, population.ref))

            explorer ! Explore(FindNumberOfEmployees)
            market.expectMsg(FindNumberOfEmployees)
        }
    }
    // TODO: cover the rest of scenarios
}
