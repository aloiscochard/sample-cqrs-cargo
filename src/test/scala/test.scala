package cargo

import java.util.Date
import org.specs2.mutable._

class Test extends Specification {
  "Cargo" should {
    "demo" in {
      import model._
      import model.command._
      import service.CargoService._

      val sf = ports.add(Port("San Fransisco")).get
      val la = ports.add(Port("Los Angeles")).get

      val cargo = cargos.add(Cargo("Refactoring")).get

      var ship = ships.add(Ship("King Roy", la, Set())).get

      ship = ships.update(Load(ship.name, cargo.name, new Date)).get
      ship = ships.update(Departure(ship.name, la.name, new Date)).get
      ship = ships.update(Arrival(ship.name, sf.name, new Date)).get

      // Print audit trail
      ships.audit("King Roy").foreach(println)

      // Was in LA ?
      ships.audit("King Roy").view.flatMap(x => x match {
        case Departure(_, la.name, _) => Some(x)
        case Arrival(_, la.name, _) => Some(x)
        case _ => None
      }).headOption must beSome

      ship.port must beEqualTo(sf)
    }
  }
}
