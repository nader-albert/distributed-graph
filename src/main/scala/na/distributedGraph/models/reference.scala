/*package na.distributedGraph.models

import akka.actor.ActorRef
import na.distributedGraph.entities.businesses.Employer

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

trait QueryBuilder {
    var one = false
    var many = false
    var entity: Entity = _

    def find(entityToBuilder: => QueryBuilder): this.type = entityToBuilder

    /*def every(entity: Person.type): this.type = {
        //this.entity = entity
        many = true
        this
    }*/

    def one(entity: Entity): this.type = {
        this.entity = entity
        one = true
        this
    }

    def `with`() = ???

    def build: Command = ???

    def transform: Command
}

class CorporateQueryBuilder extends QueryBuilder {
    var numberOfEmployees = 0

    def withEmployeesMoreThan(number: Int) = this.numberOfEmployees = number
}

trait PersonQueryBuilder extends QueryBuilder {
    var friendOf: Seq[Person] = Seq.empty
    var relativeOf: Seq[Person] = Seq.empty
    var worksAt: Option[Corporate] = None
    var isEmployed: Boolean = worksAt.isDefined

    def every(entity: Person.type): this.type = {
        //this.entity = entity
        many = true
        this
    }

    def withRelatives(persons: => Seq[Person]): this.type = {
        this.relativeOf = persons
        this
    }

    def hasFriends(persons: => Boolean): this.type = {
        //this.friendOf = persons
        this
    }

    def worksAt(employer: Corporate): this.type = {
        this.worksAt = Some(employer)
        this
    }

    def that(queryBuilder: PersonQueryBuilder): this.type = {
        queryBuilder
    }

    def employed: this.type = {
        this.isEmployed = true
        this
    }

    def every(entity: Person): this.type = {
        this.entity = entity
        many = true
        this
    }

    //def relativesOf

    def build(queryBuilder: QueryBuilder) = queryBuilder.transform

    def transform: Command = {
        //TODO: match case on all attributes and determine the command

        ListAll
    }

}*/

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