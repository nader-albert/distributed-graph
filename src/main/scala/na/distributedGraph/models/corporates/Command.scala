package na.distributedGraph.models.corporates

import akka.actor.ActorRef

trait Command

/**
  * invites a business corporate to join the market
  * */
case object Join extends Command

/**
  * informs a business corporate that it has to leave the market
  * */
case object Leave extends Command

/**
  * informs a business corporate that it has to hire a candidate
  * */
case class Hire(candidate: ActorRef) extends Command

/**
  * informs a business corporate that it has to fire a candidate
  * */
case class Fire(employee: ActorRef) extends Command

case class Add(business: ActorRef) extends Command

case object ListAll extends Command

case class Remove(business: ActorRef) extends Command
