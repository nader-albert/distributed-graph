package na.distributedGraph.events

import na.distributedGraph.commands.Offer

trait CandidateEvent

case class Accepted(offer: Offer) extends CandidateEvent

case class Rejected(reason: String) extends CandidateEvent

case object Fired extends CandidateEvent

case object Resigned extends CandidateEvent
