package cargo
package service

import model._

trait CargoService {

  trait EntitySupport[T, K] {
    def add(x: T): Option[T]
    def query(k: K): Option[T]
  }

  trait CargoSupport extends EntitySupport[Cargo, String]

  trait PortSupport extends EntitySupport[Port, String]

  trait ShipSupport extends EntitySupport[Ship, String] {
    import model.command._

    def audit(name: String): Seq[Command]

    def update(cmd: Command): Option[Ship] = query(cmd.ship).flatMap { ship =>
      cmd match {
        case load: Load => cargos.query(load.cargo).flatMap { cargo =>
          if (!ship.cargos.contains(cargo))
          Some(ship.copy(cargos = ship.cargos + cargo)) else None
        }
        case departure: Departure => ports.query(departure.port).flatMap { port =>
          if (ship.port == port) Some(ship)
          else None
        }
        case arrival: Arrival => ports.query(arrival.port).flatMap { port =>
          if (ship.port != port) Some(ship.copy(port = port))
          else None
        }
      }
    }
  }

  val cargos: CargoSupport
  val ports: PortSupport
  val ships: ShipSupport
}

object CargoService extends CargoService {
  import model.command._

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

    override def update(cmd: Command) = super.update(cmd).map(log(cmd))

    private def log(e: Command)(ship: Ship) = {
      events.get(ship.name).foreach(xs => events += ship.name -> (xs :+ e))
      states -= ship.name
      states += ship.name -> ship
      ship
    }

    private var states = Map[String, Ship]()
    private var events = Map[String, Seq[Command]]()
  }
}
