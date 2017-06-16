package na.distributedGraph.models.queries

import akka.actor.ActorRef

sealed trait Query

trait PersonQuery extends Query {
    //val entity: Entity

    //sealed trait condition
}

trait CorporateQuery extends Query

case class RelativesOf(person: Person)
case class EmployeesOf(corporate: Corporate)
case class RelativesOf(persons: EmployeesOf) extends PersonQuery
case class HasFriendsWithRelatives(employed: Boolean) extends PersonQuery
case class CorporatesWithEmployees(number: Int) extends CorporateQuery

case class SequenceOf(actorList: Seq[ActorRef])
case class MapOf(actorList: Map[ActorRef, Seq[ActorRef]])

case class SearchResult(conditionSatisfied: Boolean)

/** */
sealed trait Entity

case object Person extends Entity
case object Corporate extends Entity

case class Person(name: String) extends Entity
case class Corporate(name: String) extends Entity

trait Relation

case class Relative() extends Relation
case class Friend() extends Relation

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

trait PersonQueryBuilder { //extends QueryBuilder {
    //var friendOf: Seq[Person] = Seq.empty
    //var relativeOf: Seq[Person] = Seq.empty
    var worksAt: Option[Corporate] = None
    var isEmployed: Boolean = worksAt.isDefined
    var withFriendsEmployed = false
    var withRelativesEmployed = false

    var select: Entity = _
    var conditions: List[ConditionWord] = List.empty

    def every(person: Person.type) = new Every(person)

    def one(person: Person) = new One(person)

    def who(condition: ConditionWord) = new Who(condition)

    def `with`(condition: ConditionWord) = new With(condition)

    def worksAt(employer: Corporate) = new WorksAt(employer)

    def hasFriends(condition: ConditionWord) = new HasFriends(condition)

    def withRelatives(condition: ConditionWord) = new WithRelatives(condition)

    def employed = new Employed

    def relativesOf(matchWord: MatchWord) = new RelativesOf(matchWord)

    protected def find(entityToBuilder: => MatchWord): this.type = this

    sealed trait MatchWord {
        var one = false
        var many = false
        var `match`: Entity
    }

    sealed trait LinkWord {}
    sealed trait ConditionWord {}

    class Every(override var `match`: Person.type) extends MatchWord {
        select = `match`
    }

    class One(override var `match`: Entity) extends MatchWord {
        select = `match`
    }

    class RelativesOf(override var `match`: MatchWord) extends MatchWord {

    }

    class Who(condition: ConditionWord) extends LinkWord {}

    class With(condition: ConditionWord) extends LinkWord {}

    class WorksAt(employer: Corporate) extends ConditionWord {
        worksAt = Some(employer)
        conditions = conditions.::(this)
    }

    class HasFriends(condition: ConditionWord) extends ConditionWord {
        conditions = conditions.::(this)
    }

    class WithRelatives(condition: ConditionWord) extends ConditionWord {
        conditions = conditions.::(this)
    }

    class Employed extends ConditionWord {
        isEmployed = true
        conditions = conditions.::(this)
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
        //Seq.empty


        Explore()
    }

}