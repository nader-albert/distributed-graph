package na.distributedGraph.models

trait Command

/**
  * invites an actor to join a squad
  * */
case object Join extends Command

/**
  * informs an actor that it has been expelled out of the squad
  * */
case object Leave extends Command

/**
  * list all actors in a squad
  * */
case object ListAll extends Command
