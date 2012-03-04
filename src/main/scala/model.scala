package cargo
package model

import java.util.Date

case class Cargo(name: String)
case class Port(name: String)
case class Ship(name: String, port: Port, cargos: Set[Cargo])


package event {
  sealed trait Event
  case class LoadEvent(ship: Ship, cargo: Cargo, date: Date) extends Event
  case class DepartureEvent(ship: Ship, port: Port, date: Date) extends Event
  case class ArrivalEvent(ship: Ship, port: Port, date: Date) extends Event
}
