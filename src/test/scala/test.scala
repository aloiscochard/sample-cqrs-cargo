package cargo

import java.util.Date
import org.specs2.mutable._

class Test extends Specification {
  "Cargo" should {
    "demo" in {
      import model._
      import model.command._
      import service.CargoService._

      // Processing
      val ship = for (
        sf <- ports.add(Port("San Fransisco"));
        la <- ports.add(Port("Los Angeles"));
        cargo <- cargos.add(Cargo("Refactoring"));
        ship <- ships.add(Ship("King Roy"));
        _ <- ships.update(Arrival(ship.name, la.name, new Date));
        _ <- ships.update(Load(ship.name, cargo.name, new Date));
        _ <- ships.update(Departure(ship.name, la.name, new Date));
        last <- ships.update(Arrival(ship.name, sf.name, new Date))
      ) yield { last }

      // Print audit trail
      ships.audit("King Roy").foreach(println)

      // Was in LA ?
      ships.audit("King Roy").view.flatMap(x => x match {
        case Arrival(_, "Los Angeles", _) => Some(x)
        case _ => None
      }).headOption must beSome

      // Misc checks
      ship must beSome
      ship.map(x => x.port must beEqualTo(Some(Port("San Fransisco")))).get
    }
  }
}
