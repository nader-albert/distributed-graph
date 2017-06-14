package na.distributedGraph.entities

import akka.actor.ActorLogging
import com.typesafe.config.Config

trait Squad [T] {

    this :ActorLogging =>

    def initialise(config: Config): Unit = {
        val squadSize: Int =
            try {
                Integer.parseInt(config getString "number")
            } catch {
                case _: NumberFormatException =>
                    log error "configuration problem: invalid number "
                    0
                case _:Throwable => 0
            }

        log.info ("\r\n ************************** Adding (%s) member to the squad ************************** \r\n".format(squadSize))

        for(index <- 0 to squadSize) build(index)
    }

    def build(count: Int)
}
