package cargo
package model

import java.util.Date

case class Cargo(name: String)
case class Port(name: String)
case class Ship(name: String, port: Option[Port] = None, cargos: Set[Cargo] = Set())

package command {
  sealed trait Command { def ship: String }
  case class Load(ship: String, cargo: String, date: Date) extends Command
  case class Departure(ship: String, port: String, date: Date) extends Command
  case class Arrival(ship: String, port: String, date: Date) extends Command
}
