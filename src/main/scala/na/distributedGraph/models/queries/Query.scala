package na.distributedGraph.models.queries

import akka.actor.ActorRef
import na.distributedGraph.models.dsl.{Corporate, Person}

sealed trait Query {

}

trait PopulationQuery extends Query

//case object RelativesOfFriends extends TargetSelection

trait MarketQuery extends Query

//case class RelativesOf(person: Person)
//case class EmployeesOf(corporate: Corporate)
//case class RelativesOf(persons: EmployeesOf) extends PersonQuery
//case class HasFriendsWithRelatives(employed: Boolean) extends PersonQuery
//case class CorporatesWithEmployees(number: Int) extends CorporateQuery

case class SequenceOf(actorList: Seq[ActorRef])
case class MapOf(actorList: Map[ActorRef, Seq[ActorRef]])
case class ConditionResult(conditionSatisfied: Boolean)
case class SearchResult(number: Int)

case object Employed extends PopulationQuery
case object FindFriends extends PopulationQuery
case class FindRelativesAndReply(replyTo: ActorRef) extends PopulationQuery
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

//sealed trait Entity

//case object Person extends Entity
//case object Corporate extends Entity

//case class Person(name: String) extends Entity
//case class Corporate(name: String) extends Entity

//trait Relation

//case class Relative() extends Relation
//case class Friend() extends Relation

/*trait QueryBuilder {
    var one = false
    var many = false
    var entity: Entity = _

    protected def find(entityToBuilder: => QueryBuilder): this.type = entityToBuilder

    /*def every(entity: Person.type): this.type = {
        //this.entity = entity
        many = true
        this
    }*/

    protected def one(entity: Entity): this.type = {
        this.entity = entity
        one = true
        this
    }

    protected def `with`() = ???

    protected def build: Command = ???

    protected def transform: Command
}*/

/*class CorporateQueryBuilder extends QueryBuilder {
    var numberOfEmployees = 0

    def withEmployeesMoreThan(number: Int) = this.numberOfEmployees = number
}*/

/*
trait PersonQueryBuilder { //extends QueryBuilder {
    // var friendOf: Seq[Person] = Seq.empty
    // var relativeOf: Seq[Person] = Seq.empty
    // var worksAt: Option[Corporate] = None
    // var isEmployed: Boolean = worksAt.isDefined
    // var withFriendsEmployed = false
    // var withRelativesEmployed = false

    var target: TargetSelection = _
    var conditionOfSelection: Condition = _

    //var select: Entity = _
    //var conditions: List[ConditionWord] = List.empty

    def every(person: Person.type) = Every(person)

    def one(person: Person) = One(person)

    def who(condition: ConditionWord) = new Who(condition)

    //def `with`(condition: ConditionWord) = new With(condition)

    def worksAt(employer: Corporate) = new WorksAt(employer)

    def hasFriends(condition: ConditionWord) = new HasFriends(condition)

    def withRelatives(condition: ConditionWord) = new WithRelatives(condition)

    def employed = Employed

    def relativesOf(matchWord: MatchWord) = RelativesOf(matchWord)

    protected def find(matcher: => MatchWord): this.type = {
        target = matcher match {
            case _:Every => EveryPerson
            case One(person) => OnePerson(person)
            case RelativesOf(selection) => selection match {
                case _: Every => RelativesOfAny
                case one: One => RelativesOfOne(one.selection)
            }
        }
        this
    }

    sealed trait MatchWord {
        var one = false
        var many = false
        var selection: Entity
    }

    sealed trait LinkWord {}
    sealed trait ConditionWord {}

    case class Every(override var selection: Person.type) extends MatchWord {
        //select = `match`
        //target = EveryPerson
    }

    case class One(override var selection: Person) extends MatchWord {
        //target = OnePerson(selection)
    }

    case class RelativesOf(override var selection: MatchWord) extends MatchWord {
        //selection match {
        //    case _: Every => target = RelativesOfAny
        //    case one: One => target = RelativesOfOne(one.selection)
        //}
    }

    class Who(condition: ConditionWord) extends LinkWord {
        conditionOfSelection = condition match {
            case Employed => Employment(true)
            case WorksAt(corporate) => EmployedBy(corporate)
            case HasFriends(withFriendCondition) => withFriendCondition match {
                case WithRelatives(withRelativeCondition) => withRelativeCondition match {
                    case Employed => HasFriendsWithRelatives(Employment(true))
                }
            }
        }
    }

    //class With(condition: ConditionWord) extends LinkWord {}

    class WorksAt(employer: Corporate) extends ConditionWord {
        //worksAt = Some(employer)
        //conditions = conditions.::(this)
        //condition = EmployedBy(employer)
    }

    case object Employed extends ConditionWord {
        //isEmployed = true
        //conditions = conditions.::(this)
        //condition = Employment(true)
    }

    case class HasFriends(condition: ConditionWord) extends ConditionWord {
        //conditions = conditions.::(this)
    }

    case class WithRelatives(condition: ConditionWord) extends ConditionWord {
        //conditions = conditions.::(this)
    }

    /*protected def one(entity: Entity): this.type = {
        this.entity = entity
        one = true
        this
    }*/

    /*def withRelatives(persons: => Seq[Person]): this.type = {
        this.relativeOf = persons
        this
    }

    def hasFriends(persons: => Boolean): this.type = {
        //this.friendOf = persons
        this
    }*/

    /*def worksAt(employer: Corporate): this.type = {
        this.worksAt = Some(employer)
        this
    }*/

    /*def who(queryBuilder: PersonQueryBuilder): this.type = {
        queryBuilder
    }*/

    /*def employed: this.type = {
        this.isEmployed = true
        this
    }*/


    //def relativesOf

    def build: Command = this.transform

    private def transform: Command = {
        target match {

        }
        //Seq.empty

        //Explore()
    }

}*/

/*
* sealed trait Condition
case class Employment(status: Boolean) extends Condition
case class EmployedBy(corporate: Corporate) extends Condition
case class HasFriendsWithRelatives(employment: Employment) extends Condition

sealed trait TargetSelection
case object EveryPerson extends TargetSelection
case class OnePerson(person: Person) extends TargetSelection
case class RelativesOfOne(person: Person) extends TargetSelection
case object RelativesOfAny extends TargetSelection*/