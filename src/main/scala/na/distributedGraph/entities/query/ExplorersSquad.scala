package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.Config
import na.distributedGraph.entities.Squad
import na.distributedGraph.entities.query.ExplorersSquad.waitTime
import na.distributedGraph.models.queries.{Add, Explore, Remove, Run}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

class ExplorersSquad(queryConfig: Config) extends Squad[Explorer] with Actor with ActorLogging {

    var explorers: List[ActorRef] = List.empty[ActorRef]

    val market: ActorRef = Await.result(context.actorSelection("../market").resolveOne(waitTime), waitTime)
    val population: ActorRef = Await.result(context.actorSelection("../population").resolveOne(waitTime), waitTime)

    initialise(queryConfig)

    /***
      * receives a query, pick up a random Explorer and sends it the message to ditribute the load
      * */
    override def receive: Receive = {
        case Add(explorer) => explorers.::(explorer) //TODO: check we need to have the explorer acknowledge being added to the team

        case Remove(explorer) => explorers = explorers.filterNot(_ == explorer)
        //TODO: check we need to have the explorer acknowledge being added to the team

        //TODO: A trivial routing algorithm implementation... should try to send to a free explorer in the squad, for better resource utilisation
        // several queries might end up be sitting in the mailbox of few explorers while others might be with idle hands
        // distribute the list evenly on the list of available explorers
        case Run(queries) => explorers.drop(Random.nextInt(explorers.size)).head forward Explore(explore)
    }

    override def build(explorerIndex: Int): Unit = {
        val newExplorer = context.actorOf(Explorer.props(market), name = "Explorer-" + explorerIndex)

        explorers = newExplorer :: explorers
    }
}

object ExplorersSquad {

    val waitTime: FiniteDuration = 5 seconds
    def props(queryConfig: Config) = Props(classOf[ExplorersSquad], queryConfig)
}
