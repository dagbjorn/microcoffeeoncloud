package study.microcoffee.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.language.postfixOps

class MenuApiTest extends Simulation {

  private val baseUrl: String = readProperty("app.baseUrl")
  private val numberOfUsers: Int = readProperty("test.numberOfUsers", "1").toInt
  private val durationSeconds: Int = readDurationAsSeconds("test.durationMinutes", "test.durationSeconds", "1")

  println(s"baseUrl: $baseUrl")
  println(s"numberOfUsers: $numberOfUsers")
  println(s"durationSeconds: $durationSeconds")

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")

  private val scn = scenario("Menu API")
    .exec(http("Get menu")
      .get("/api/coffeeshop/menu")
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
