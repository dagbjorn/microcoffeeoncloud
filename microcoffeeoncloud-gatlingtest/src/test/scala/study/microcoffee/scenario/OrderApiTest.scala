package study.microcoffee.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.protocol.HttpProtocolBuilder
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._

class OrderApiTest extends Simulation {

  implicit val formats = DefaultFormats

  private val baseUrl: String = readProperty("app.baseUrl")
  private val numberOfUsers: Int = readProperty("app.numberOfUsers", "1").toInt
  private val durationMinutes: Int = readProperty("app.durationMinutes", "1").toInt

  println(s"baseUrl: $baseUrl")
  println(s"numberOfUsers: $numberOfUsers")
  println(s"durationMinutes: $durationMinutes")

  val orderFeeder = csv("orders.csv")
    .circular
    .convert({
      case ("drinker", value)         => value.trim()
      case ("size", value)            => value.trim()
      case ("drinkName", value)       => value.trim()
      case ("drinkFamily", value)     => value.trim()
      case ("selectedOptions", value) => write(value.trim().split("\\+"))
    })

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")

  val scn: ScenarioBuilder = scenario("Order API")
    .feed(orderFeeder)
    .exec(http("Submit order")
      .post("/api/coffeeshop/${coffeeShopId}/order")
      .header("Content-Type", "application/json")
      .body(ElFileBody("OrderTemplate.json")).asJson
      .check(status.is(201))
      .check(header("Location").exists.saveAs("location")))

    .exec(http("Get order")
      .get("${location}")
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
