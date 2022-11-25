package study.microcoffee.scenario

import io.gatling.app.Gatling

object OrderApiTestRunner extends App {

  Gatling.main(Array("-s", "study.microcoffee.scenario.OrderApiTest", "-rf", "target/gatling"))
}
