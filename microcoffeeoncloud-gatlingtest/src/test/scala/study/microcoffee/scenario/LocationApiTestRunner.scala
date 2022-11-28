package study.microcoffee.scenario

import io.gatling.app.Gatling

object LocationApiTestRunner extends App {

  Gatling.main(Array("-s", "study.microcoffee.scenario.LocationApiTest", "-rf", "target/gatling"))
}
