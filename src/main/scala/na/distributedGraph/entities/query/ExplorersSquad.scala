package na.distributedGraph.entities.query

`import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Identify, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.models.queries.Add

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

import scala.concurrent.duration.FiniteDuration

class ExplorersSquad(queryConfig: Config) extends Squad[Explorer] with Actor with ActorLogging {

    var explorers: List[ActorRef] = List.empty[ActorRef]

    initialise(queryConfig)

    /***
      * receives a query, pick up a random Explorer and sends it the message to ditribute the load
      * */
    override def receive: Receive = {
        case Add(explorer) => explorers.::(explorer) //TODO: check we need to have the explorer acknowledge being added to the team
    }

    override def build(explorerIndex: Int): Unit = {
        import ExplorersSquad._

        val market = Await.result(context.actorSelection("../market").resolveOne(waitTime), waitTime)

        val newExplorer = context.actorOf(Explorer.props(market), name = "Explorer-" + explorerIndex)

        explorers = newExplorer :: explorers
    }
}

object ExplorersSquad {

    val waitTime: FiniteDuration = 5 seconds
    def props(queryConfig: Config) = Props(classOf[ExplorersSquad], queryConfig)
}
