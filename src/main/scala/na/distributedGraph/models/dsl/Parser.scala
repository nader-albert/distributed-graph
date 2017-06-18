package na.distributedGraph.models.dsl

import na.distributedGraph.models.queries._

sealed trait Entity

case class Person(name: String) extends Entity
case class Corporate(name: String) extends Entity

sealed trait Condition
case class Employment(status: Boolean) extends Condition
case class EmployeesExceed(number: Int) extends Condition
case class EmployedBy(corporate: Corporate) extends Condition
case class HasFriendsWithRelatives(employment: Employment) extends Condition

sealed trait TargetSelection
case object EveryPerson extends TargetSelection
case object EveryBusiness extends TargetSelection
case class OnePerson(person: Person) extends TargetSelection
case class RelativesOfOne(person: Person) extends TargetSelection
case object RelativesOfAny extends TargetSelection

trait Parser {
    var targetSelection: TargetSelection = _
    var conditionOfSelection: Option[Condition] = None

    def every(person: Person.type) = Every(person)

    def every(business: Corporate.type) = EveryCorporate(business)

    def one(person: Person) = One(person)

    def who(condition: ConditionWord) = new Who(condition)

    def `with`(condition: ConditionWord) = new With(condition)

    def worksAt(employer: Corporate) = WorksAt(employer)

    def hasFriends(condition: ConditionWord) = HasFriends(condition)

    def withRelatives(condition: ConditionWord) = WithRelatives(condition)

    def numberOfEmployeesMoreThan(number: Int) = NumberOfEmployeesMoreThan(number)

    def employed = Employed

    def relativesOf(matchWord: MatchWord) = RelativesOf(matchWord)

    protected def find(matcher: => MatchWord): this.type = {
        targetSelection = matcher match {
            case _:Every => EveryPerson
            case _:EveryCorporate => EveryBusiness
            case One(person) => OnePerson(person)
            case RelativesOf(selection) => selection match {
                case _: Every => RelativesOfAny
                case one: One => RelativesOfOne(one.selection)
                case _ => RelativesOfAny //Should be a correct default value
            }
        }
        this
    }

    sealed trait LinkWord
    sealed trait MatchWord
    sealed trait ConditionWord

    case class One(selection: Person) extends MatchWord
    case class Every(selection: Person.type) extends MatchWord
    case class EveryCorporate(selection: Corporate.type) extends MatchWord
    case class RelativesOf(selection: MatchWord) extends MatchWord

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

    class With(condition: ConditionWord) extends LinkWord {
        conditionOfSelection =
            condition match {
                case employees: NumberOfEmployeesMoreThan => Some(EmployeesExceed(employees.number))
                case _ => None
            }
    }

    case class NumberOfEmployeesMoreThan(number: Int) extends ConditionWord

    case class WorksAt(employer: Corporate) extends ConditionWord

    case object Employed extends ConditionWord

    case class HasFriends(condition: ConditionWord) extends ConditionWord

    case class WithRelatives(condition: ConditionWord) extends ConditionWord

    def build: Query = this.transform.orNull

    private def transform: Option[Query] = {
        (targetSelection, conditionOfSelection) match {
            case (RelativesOfOne(person), None) => Some(FindRelativesOf(person))
            case (EveryBusiness, Some(EmployeesExceed(number))) => Some(FindCorporatesWithEmployeesMoreThan(number))
            case (RelativesOfAny, Some(EmployedBy(corporate))) => Some(FindRelativesOfWhoWorksAt(corporate))
            case (EveryPerson, Some(EmployedBy(corporate))) => Some(FindPersonsWhoWorkAt(corporate))
            case (EveryPerson, Some(HasFriendsWithRelatives(employment))) => Some(FindPersonsWithFriendsHavingRelatives(employment.status))
            case _ => println("DSLParser: unexpected combination of selection and condition ") ; None
        }
    }
}
