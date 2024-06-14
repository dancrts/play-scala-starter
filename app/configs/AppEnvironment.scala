package configs

import enumeratum._
sealed trait AppEnvironment extends EnumEntry

object AppEnvironment extends Enum[AppEnvironment] {

    val values = findValues

    case object Production extends AppEnvironment

    case object Development extends AppEnvironment

    case object Sandbox extends AppEnvironment

    case object Test extends AppEnvironment
}