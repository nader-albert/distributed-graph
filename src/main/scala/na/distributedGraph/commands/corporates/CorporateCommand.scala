package na.distributedGraph.commands.corporates

import akka.actor.ActorRef

trait CorporateCommand

/**
  * invites a business corporate to join the market
  * */
case object Join extends CorporateCommand

/**
  * informs a business corporate that it has to leave the market
  * */
case object Leave extends CorporateCommand

/**
  * informs a business corporate that it has to hire a candidate
  * */
case class Hire(candidate: ActorRef) extends CorporateCommand

/**
  * informs a business corporate that it has to fire a candidate
  * */
case class Fire(employee: ActorRef) extends CorporateCommand

