package na.distributedGraph.models.corporates

import na.distributedGraph.models.Offer

trait Event

case object Joined extends Event

case object Left extends Event

case class Accepted(offer: Offer) extends Event

case class Rejected(offer: Offer, reason: String) extends Event

case object Fired extends Event

case object Resigned extends Event
