package na.distributedGraph.entities.query

import akka.actor.{Actor, ActorLogging, Props}

class Explorer extends Actor with ActorLogging {

    override def receive: Receive = ???
}

object Explorer {

    def props() = Props(classOf[Explorer])
}