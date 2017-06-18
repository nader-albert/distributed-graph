package na.distributedGraph

import akka.actor.ActorSystem
import akka.testkit.TestKit.shutdownActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import na.distributedGraph.entities.businesses.{Employer, Market}
import na.distributedGraph.models.Offer
import na.distributedGraph.models.corporates.{Add, Hire}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.util.Random

class EmployerSpec() extends TestKit(ActorSystem("EmployerSpec"))
    with Matchers
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll {

    override def afterAll {
        shutdownActorSystem(system)
    }

    "An Employer " must {
        "send an offer to an employee when wanting to hire him" in {
            val employee = TestProbe()

            val number = 60
            val config = ConfigFactory.parseString("number = %s".format(number))

            val market = system.actorOf(Market.props(config))
            val newBusiness = system.actorOf(Employer.props(Random.nextInt(10)), "Test-Corporate-1")

            market ! Add(newBusiness)

            Thread.sleep(1000)

            newBusiness ! Hire(employee.ref)

            employee.expectMsgPF(10 seconds)({ case _: Offer => } )
        }
    }

    //TODO: cover the rest of scenarios - different PopulationQueries

}
