package study.microcoffee.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.protocol.HttpProtocolBuilder

class LocationApiTest extends Simulation {

  private val baseUrl: String = readProperty("app.baseUrl")
  private val numberOfUsers: Int = readProperty("app.numberOfUsers", "1").toInt
  private val durationMinutes: Int = readProperty("app.durationMinutes", "1").toInt

  println(s"baseUrl: $baseUrl")
  println(s"numberOfUsers: $numberOfUsers")
  println(s"durationMinutes: $durationMinutes")

  val locationFeeder = csv("locations.csv").circular

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")

  val scn: ScenarioBuilder = scenario("Location API")
    .feed(locationFeeder)
    .exec(http("Find coffeeshop")
      .get("/api/coffeeshop/nearest/${latitude}/${longitude}/${distance}")
      .check(status.is(200)))

  setUp(
    scn.inject(constantUsersPerSec(numberOfUsers) during (durationMinutes minutes))).protocols(httpProtocol)

  //
  // Helpers
  //

  private def readProperty(propertyName: String, defaultValue: String = null): String = {
    var value = System.getProperty(propertyName, defaultValue)
    if (value == null) {
      throw new Error(s"$propertyName is missing")
    }
    return value
  }
}
