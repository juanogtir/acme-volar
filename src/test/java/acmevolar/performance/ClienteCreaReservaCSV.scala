package acmevolar.model;

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ClienteCreaReservaCSV extends Simulation {

	val httpProtocol = http
		.baseUrl("http://www.dp2.com")
		.inferHtmlResources(BlackList(""".*.css""", """.*.js""", """.*.ico""", """.*.png"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
		.doNotTrackHeader("1")
		.upgradeInsecureRequestsHeader("1")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0")

    val csvFeeder = csv("books.csv").random

	val headers_3 = Map(
		"Origin" -> "http://www.dp2.com")

	object Home {
		val home=exec(http("Home")
			.get("/"))
		.pause(10)
	}
// i.g.h.a.HttpRequestAction - 'httpRequest-42' failed to execute: No attribute named 'stoken' is defined
	object LoginFormClient{
		val loginFormClient = exec(http("LoginFormClient")
			.get("/login")
			.check(css("input[name=_csrf]", "value").saveAs("stoken"))	
		).pause(16)
		.exec(http("LoggedClient")
			.post("/login")
			.headers(headers_3)
			.formParam("username", "client1")
			.formParam("password", "client1")
			.formParam("_csrf", "${stoken}"))
		.pause(16)
	}

	object CreateBook{
		val createBook=exec(http("CreateBook")
			.get("/books/3/new")
			.check(css("input[name=_csrf]", "value").saveAs("stoken"))	
		).pause(18)
        .feed(csvFeeder)
            .exec(http("BookCreated")
                .post("/books/3/new")
                .headers(headers_3)
                .formParam("quantity", "${quantity}")
                .formParam("price", "${price}")
                .formParam("moment", "")
                .formParam("_csrf", "${stoken}"))
		.pause(10)
	}

	val scnBookClientCreate = scenario("Clients create books").exec(Home.home,LoginFormClient.loginFormClient, CreateBook.createBook)
	
	setUp(
		scnBookClientCreate.inject(rampUsers(50) during (100 seconds))
	).protocols(httpProtocol)
	.assertions(global.responseTime.max.lt(5000),forAll.failedRequests.percent.lte(5),global.successfulRequests.percent.gt(90)).maxDuration(10 minutes)

}