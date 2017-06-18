package na.distributedGraph

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.testkit.TestKit.shutdownActorSystem
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class PersonSpec  extends TestKit(ActorSystem("PersonSpec"))
    with Matchers
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll {

    override def afterAll {
        shutdownActorSystem(system)
    }

    //TODO: to be written
}
