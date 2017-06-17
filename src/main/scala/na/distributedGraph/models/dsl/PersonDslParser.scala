package na.distributedGraph.models.dsl

import na.distributedGraph.models.queries._

sealed trait Entity

//case object Person extends Entity
//case object Corporate extends Entity

case class Person(name: String) extends Entity
case class Corporate(name: String) extends Entity

sealed trait Condition
case class Employment(status: Boolean) extends Condition
case class EmployedBy(corporate: Corporate) extends Condition
case class HasFriendsWithRelatives(employment: Employment) extends Condition

sealed trait TargetSelection
case object EveryPerson extends TargetSelection
case class OnePerson(person: Person) extends TargetSelection
case class RelativesOfOne(person: Person) extends TargetSelection
case object RelativesOfAny extends TargetSelection

trait PersonDslParser { //extends QueryBuilder {
    // var friendOf: Seq[Person] = Seq.empty
    // var relativeOf: Seq[Person] = Seq.empty
    // var worksAt: Option[Corporate] = None
    // var isEmployed: Boolean = worksAt.isDefined
    // var withFriendsEmployed = false
    // var withRelativesEmployed = false

    var targetSelection: TargetSelection = _
    var conditionOfSelection: Option[Condition] = None

    //var select: Entity = _
    //var conditions: List[ConditionWord] = List.empty

    def every(person: Person.type) = Every(person)

    def one(person: Person) = One(person)

    def who(condition: ConditionWord) = new Who(condition)

    def worksAt(employer: Corporate) = WorksAt(employer)

    def hasFriends(condition: ConditionWord) = HasFriends(condition)

    def withRelatives(condition: ConditionWord) = WithRelatives(condition)

    def employed = Employed

    def relativesOf(matchWord: MatchWord) = RelativesOf(matchWord)

    protected def find(matcher: => MatchWord): this.type = {
        targetSelection = matcher match {
            case _:Every => EveryPerson
            case One(person) => OnePerson(person)
            case RelativesOf(selection) => selection match {
                case _: Every => RelativesOfAny
                case one: One => RelativesOfOne(one.selection)
                case _ => RelativesOfAny //Should be a correct default value
            }
        }
        this
    }

    sealed trait MatchWord {
        //var one = false
        //var many = false
        //var selection: Entity
    }

    sealed trait LinkWord {}
    sealed trait ConditionWord {}

    case class Every(selection: Person.type) extends MatchWord {
        //select = `match`
        //target = EveryPerson
    }

    case class One(selection: Person) extends MatchWord {
        //target = OnePerson(selection)
    }

    case class RelativesOf(selection: MatchWord) extends MatchWord {
        //selection match {
        //    case _: Every => target = RelativesOfAny
        //    case one: One => target = RelativesOfOne(one.selection)
        //}
    }

    class Who(condition: ConditionWord) extends LinkWord {
        conditionOfSelection =

            condition match {
                case Employed => Some(Employment(true))
                case WorksAt(corporate) => Some(EmployedBy(corporate))
                case HasFriends(withFriendCondition) => withFriendCondition match {
                    case WithRelatives(withRelativeCondition) => withRelativeCondition match {
                        case Employed => Some(HasFriendsWithRelatives(Employment(true)))
                        case _ => None //should be a correct default value..
                    }
                    case _ => None //should be a correct default value.. made temporarily to avoid match being inexhaustive
                }
                case _ => println("DSLParser: unexpected condition"); None
            }
    }

    case class WorksAt(employer: Corporate) extends ConditionWord {
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

    def build: Query = this.transform.orNull

    private def transform: Option[Query] = {
        (targetSelection, conditionOfSelection) match {
            case (RelativesOfOne(person), None) => Some(FindRelativesOf(person))
            // TODO: Search For a Business
            case (RelativesOfAny, Some(EmployedBy(corporate))) => Some(FindRelativesOfWhoWorksAt(corporate))
            case (EveryPerson, Some(EmployedBy(corporate))) => Some(FindPersonsWhoWorkAt(corporate))
            case (EveryPerson, Some(HasFriendsWithRelatives(employment))) => Some(FindPersonsWithFriendsHavingRelatives(employment.status))
            case _ => println("DSLParser: unexpected combination of selection and condition ") ; None
        }
    }
}

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
