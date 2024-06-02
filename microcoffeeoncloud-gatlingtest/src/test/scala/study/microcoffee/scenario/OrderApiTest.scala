package study.microcoffee.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import org.json4s._
import org.json4s.jackson.Serialization._
import scala.language.postfixOps

class OrderApiTest extends Simulation {

  implicit val formats: DefaultFormats.type = DefaultFormats

  private val baseUrl: String = readProperty("app.baseUrl")
  private val numberOfUsers: Int = readProperty("test.numberOfUsers", "1").toInt
  private val durationSeconds: Int = readDurationAsSeconds("test.durationMinutes", "test.durationSeconds", "1")

  println(s"baseUrl: $baseUrl")
  println(s"numberOfUsers: $numberOfUsers")
  println(s"durationSeconds: $durationSeconds")

  private val orderFeeder = csv("orders.csv")
    .circular
    .transform({
      case ("drinker", value)         => value.trim()
      case ("size", value)            => value.trim()
      case ("drinkName", value)       => value.trim()
      case ("drinkFamily", value)     => value.trim()
      case ("selectedOptions", value) => write(value.trim().split("\\+"))
    })

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")

  private val scn = scenario("Order API")
    .feed(orderFeeder)

    .exec(http("Get CSRF token")
      .get("/api/coffeeshop/#{coffeeShopId}/order/9999999999")
      .check(status.in(200, 204))
      .check(header("X-XSRF-TOKEN").exists.saveAs("csrfToken")))

    .exec(http("Submit order")
      .post("/api/coffeeshop/#{coffeeShopId}/order")
      .header("Content-Type", "application/json")
      .header("Cookie", "XSRF-TOKEN=#{csrfToken}")
      .header("X-XSRF-TOKEN", "#{csrfToken}")
      .body(ElFileBody("OrderTemplate.json")).asJson
      .check(status.is(201))
      .check(header("Location").exists.saveAs("location")))

    .exec(http("Get order")
      .get("#{location}")
      .check(status.is(200)))

  setUp(
    scn.inject(constantUsersPerSec(numberOfUsers) during (durationSeconds seconds))).protocols(httpProtocol)

  //
  // Helpers
  //

  private def readProperty(propertyName: String, defaultValue: String = null): String = {
    val value = System.getProperty(propertyName, defaultValue)
    if (value == null) {
      throw new Error(s"$propertyName is missing")
    }
    return value
  }

  private def readDurationAsSeconds(minutesPropertyName: String, secondsPropertyName: String, defaultSeconds: String = null): Int = {
    var durationSeconds = 0

    val valueMinutes = System.getProperty(minutesPropertyName)
    if (valueMinutes != null) {
      durationSeconds = valueMinutes.toInt * 60
    }

    val valueSeconds = System.getProperty(secondsPropertyName)
    if (valueSeconds != null) {
      durationSeconds = durationSeconds + valueSeconds.toInt
    }

    if (valueMinutes == null && valueSeconds == null) {
      durationSeconds = defaultSeconds.toInt
    }

    return durationSeconds
  }
}
