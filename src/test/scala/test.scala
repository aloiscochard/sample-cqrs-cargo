package cargo

import java.util.Date
import org.specs2.mutable._

class Test extends Specification {
  "Cargo" should {
    "demo" in {
      import model._
      import model.event._
      import service.CargoService._

      val sf = ports.add(Port("San Fransico")).get
      val la = ports.add(Port("Los Angeles")).get

      val cargo = cargos.add(Cargo("Refactoring")).get

      var ship = ships.add(Ship("King Roy", la, Set())).get

      ship = ships.load(LoadEvent(ship, cargo, new Date)).get
      ship = ships.departure(DepartureEvent(ship, la, new Date)).get
      ship = ships.arrival(ArrivalEvent(ship, sf, new Date)).get

      ships.audit("King Roy").foreach(println)

      ship.port must beEqualTo(sf)
    }
  }
}
