package na.distributedGraph

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import na.distributedGraph.entities.persons.{Person, Population}
import na.distributedGraph.models.queries.{FindCorporatesWithEmployeesMoreThan, SequenceOf}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import TestKit._
import na.distributedGraph.entities.businesses.{Employer, Market}
import na.distributedGraph.models.ListAll
import na.distributedGraph.models.corporates.{Add, Hire}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class MarketSpec extends TestKit(ActorSystem("MarketSpec"))
    with Matchers
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll {

    override def afterAll {
        shutdownActorSystem(system)
    }

    "A Market Supervisor " must {
        "build a number of corporates equivalent to the configured number" in {
            val number = 20
            val config = ConfigFactory.parseString("number = %s".format(number))

            val market = system.actorOf(Market.props(config))

            Thread.sleep(100)

            market ! ListAll
            expectMsgPF(2 seconds)({ case SequenceOf(corporates) if corporates.length-1 == number => } )
        }
    }

    "A Market Supervisor " must {
        "return and empty sequence, when asked about a company having more than 1 employees; if no company in " +
            "the market started hiring " in {
            val number = 60
            val config = ConfigFactory.parseString("number = %s".format(number))

            val market = system.actorOf(Market.props(config), "Market-1")

            market ! FindCorporatesWithEmployeesMoreThan(1)
            expectMsgPF(15 seconds)({ case SequenceOf(corporates) if corporates.isEmpty => } )
        }
    }

    "A Market Supervisor " must {
        "must accept a new business to join the market after it has been initialised" in {
            val number = 60
            val config = ConfigFactory.parseString("number = %s".format(number))

            val market = system.actorOf(Market.props(config), "Market-2")

            val corporateName = "Test-Corporate-1"

            val newBusiness = system.actorOf(Employer.props(Random.nextInt(10)), corporateName)

            market ! Add(newBusiness)

            Thread.sleep(1000)

            market ! ListAll
            expectMsgPF(15 seconds)({ case SequenceOf(corporates) if corporates.length == number + 2 => })
        }
    }

    "A Market Supervisor " must {
        "return a non empty sequence, when asked about a company having more than 1 employees; if there exists at " +
            "least one in the market " in {
            val number = 60
            val config = ConfigFactory.parseString("number = %s".format(number))

            val market = system.actorOf(Market.props(config), "Market-3")

            val newBusiness = system.actorOf(Employer.props(Random.nextInt(10)), "Test-Corporate-11")
            val newEmployee = system.actorOf(Person.props(Random.nextInt(10)), "Test-Employee-1")
            val anotherEmployee = system.actorOf(Person.props(Random.nextInt(10)), "Test-Employee-2")
            val oneMoreEmployee = system.actorOf(Person.props(Random.nextInt(10)), "Test-Employee-3")

            market ! Add(newBusiness)

            Thread.sleep(1000) //Allowing time for the handshaking process associated with a new business joining the market

            newBusiness ! Hire(newEmployee)
            newBusiness ! Hire(anotherEmployee)
            newBusiness ! Hire(oneMoreEmployee)

            Thread.sleep(500) //Allowing time for the Hiring process to be complete - entails an offer package accpet or reject

            market ! FindCorporatesWithEmployeesMoreThan(0)
            expectMsgPF(30 seconds)({ case SequenceOf(corporates) if corporates.nonEmpty => } )
        }
    }
    //TODO: Cover the rest of possible scenarios
}
