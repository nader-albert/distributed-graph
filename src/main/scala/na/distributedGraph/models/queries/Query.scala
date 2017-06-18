package na.distributedGraph.models.queries

import akka.actor.ActorRef
import na.distributedGraph.models.dsl.{Corporate, Person}

sealed trait Query

trait PopulationQuery extends Query

trait MarketQuery extends Query


case class SequenceOf(actorList: Seq[ActorRef])
case class MapOf(actorList: Map[ActorRef, Seq[ActorRef]])
case class ConditionResult(conditionSatisfied: Boolean)
case class SearchResult(number: Int)

case object Employed extends PopulationQuery
case object FindFriends extends PopulationQuery
case class FindRelativesAndReplyTo(replyTo: ActorRef) extends PopulationQuery
case class DoesWorkAt(corporate: Corporate) extends Query

case class FindFriends(employed: Boolean) extends PopulationQuery
case class FindRelativesOf(person: Person) extends PopulationQuery
case class FindRelatives(employed: Boolean) extends PopulationQuery
case class FindPersonsWhoWorkAt(corporate: Corporate) extends PopulationQuery
case class FindRelativesOfWhoWorksAt(corporate: Corporate) extends PopulationQuery
case class FindFriendsHavingRelatives(employed: Boolean) extends PopulationQuery
case class FindPersonsWithFriendsHavingRelatives(employed: Boolean) extends PopulationQuery

case class FindCorporatesWithEmployeesMoreThan(number: Int) extends MarketQuery
case object FindNumberOfEmployees extends MarketQuery