package na.distributedGraph.models

import akka.actor.ActorRef
import na.distributedGraph
import na.distributedGraph.entities.businesses.Employer
import na.distributedGraph.models

trait Message

case class SearchResult(actorList: Seq[ActorRef])

trait Entity

case class Corporate(name: String) extends Entity
case class Employee() extends Entity
case class Person(name: String) extends Entity

trait Relation

case class Relative() extends Relation
case class Friend() extends Relation

case class Query ()

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
    //var worksAt: Option[Corporate] = None
    //var isEmployed: Boolean = worksAt.isDefined

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
        var `match`: Any
    }

    sealed trait LinkWord {}
    sealed trait ConditionWord {}

    class Every(override var `match`: Person.type) extends MatchWord {
        //many = true
    }

    class One(override var `match`: Entity) extends MatchWord {
        //one = true
    }

    class RelativesOf(override var `match`: MatchWord) extends MatchWord {

    }

    class Who(condition: ConditionWord) extends LinkWord {

    }

    class With(condition: ConditionWord) extends LinkWord {

    }

    class HasFriends(condition: ConditionWord) extends ConditionWord {

    }


    class WorksAt(employer: Corporate) extends ConditionWord {

    }

    /*class WithRelatives(employer: Corporate) extends ConditionWord {

    }*/

    class WithRelatives(condition: ConditionWord) extends ConditionWord {

    }

    class Employed extends ConditionWord {

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

    def build = this.transform

    def transform: Command = {
        //TODO: match case on all attributes and determine the command

        ListAll
    }

}

/*trait CorporateSupport {

    def employees

    def of(corporate: Employer)

    override def toString: String = {
        "QUERY"
    }
}*/

/*trait PersonSupport {

    val relatives: Option[String]
    val friends: Option[String]

    def isEmployed

    def worksAt(corporate: Employer)

    def of(person: Person)

    override def toString: String = {
        "QUERY"
    }
}*/

/*object PersonQuery {

    def apply: Query = new Query("")

    def build(): Command
}*/
