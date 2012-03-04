package cargo
package service

import model._

trait CargoService {
  import model.event._

  trait EntitySupport[T, K] {
    def add(x: T): Option[T]
    def query(k: K): Option[T]
  }

  trait CargoSupport extends EntitySupport[Cargo, String]

  trait PortSupport extends EntitySupport[Port, String]

  trait ShipSupport extends EntitySupport[Ship, String] {
    def audit(name: String): Seq[Event]

    def load(e: LoadEvent): Option[Ship] =
      if (!e.ship.cargos.contains(e.cargo)) Some(e.ship.copy(cargos = e.ship.cargos + e.cargo))
      else None

    def departure(e: DepartureEvent): Option[Ship] =
      if (e.ship.port == e.port) Some(e.ship)
      else None

    def arrival(e: ArrivalEvent): Option[Ship] = 
      if (e.ship.port !=  e.port) Some(e.ship.copy(port = e.port))
      else None
  }

  val cargos: CargoSupport
  val ports: PortSupport
  val ships: ShipSupport
}

object CargoService extends CargoService {
  import model.event._

  override val cargos = new CargoSupport {
    override def add(cargo: Cargo) =
      if (!states.contains(cargo.name)) {
        states += cargo.name -> cargo
        Some(cargo)
      } else None

    override def query(name: String) = states.get(name)

    private var states = Map[String, Cargo]()
  }

  override val ports = new PortSupport {
    override def add(port: Port) =
      if (!states.contains(port.name)) {
        states += port.name -> port
        Some(port)
      } else None

    override def query(name: String) = states.get(name)

    private var states = Map[String, Port]()
  }

  override val ships = new ShipSupport {
    override def add(ship: Ship) = {
      events += ship.name -> Nil
      if (!states.contains(ship.name)) {
        states += ship.name -> ship
        Some(ship)
      } else None
    }

    override def query(name: String) = states.get(name)

    override def audit(name: String) = events.get(name).getOrElse(Nil)

    override def load(e: LoadEvent) = super.load(e).map(log(e))
    override def departure(e: DepartureEvent) = super.departure(e).map(log(e))
    override def arrival(e: ArrivalEvent) = super.arrival(e).map(log(e))

    private def log(e: Event)(ship: Ship) = {
      events.get(ship.name).foreach(xs => events += ship.name -> (xs :+ e))
      states -= ship.name
      states += ship.name -> ship
      ship
    }

    private var states = Map[String, Ship]()
    private var events = Map[String, Seq[Event]]()
  }
}
