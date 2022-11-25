package study.microcoffee.scenario

import io.gatling.app.Gatling

object MenuApiTestRunner extends App {

  Gatling.main(Array("-s", "study.microcoffee.scenario.MenuApiTest", "-rf", "target/gatling"))
}
