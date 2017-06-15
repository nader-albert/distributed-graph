package na.distributedGraph.models

import akka.actor.ActorRef
import na.distributedGraph.entities.businesses.Employer

trait Message

case class SearchResult(actorList: Seq[ActorRef])

trait Entity

case class Corporate() extends Entity
case class Employee() extends Entity
case class Person() extends Entity

trait Relation

case class Relative() extends Relation
case class Friend() extends Relation

case class Query ()

trait QueryBuilder {
    var one = false
    var many = false
    var entity: Entity = _

    def find(entityToBuilder: => QueryBuilder): this.type = entityToBuilder

    def every(entity: Entity): this.type = {
        this.entity = entity
        many = true
        this
    }

    def one(entity: Entity): this.type = {
        this.entity = entity
        one = true
        this
    }

    def `with`() = ???

    def build: Command = ???
}

class CorporateQueryBuilder extends QueryBuilder {
    var numberOfEmployees = 0

    def withEmployeesMoreThan(number: Int) = this.numberOfEmployees = number

    find(every(Person())).build
}

class PersonQueryBuilder extends QueryBuilder {
    var friendOf: Seq[Person] = Seq.empty
    var relativeOf: Seq[Person] = Seq.empty
    var worksAt: Option[Corporate] = None
    var isEmployed: Boolean = worksAt.isDefined

    def withRelatives(persons: => Seq[Person]): this.type = {
        this.relativeOf = persons
        this
    }

    def hasFriends(persons: => Seq[Person]): this.type = {
        this.friendOf = persons
        this
    }

    def employedAt(employer: Corporate): this.type = {
        this.worksAt = Some(employer)
        this
    }

    def employed: this.type = {
        this.isEmployed = true
        this
    }

    find(every(Person())).build
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
