package study.microcoffee.location.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import study.microcoffee.location.test.utils.MongoDBUtils;

/**
 * Integration tests of {@link LocationController} based on {@link WebTestClient}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties", properties = "server.ssl.enabled=false")
@ActiveProfiles("itest")
@Profile("itest")
@DirtiesContext // Fixes "Address already in use: bind" for port 8081
class LocationControllerWebClientIT {

    private static final String SERVICE_PATH = "/api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}";

    @Autowired
    private MongoTemplate mongoTemplate;

    // SSL must be disabled to get a correct URL injected. (Otherwise it will be a http URL with SSL port.)
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void init() throws Exception {
        MongoDBUtils.loadCoffeeshopCollection(mongoTemplate, "testdata/coffeeshop.json");
    }

    @AfterEach
    void destroy() {
        MongoDBUtils.dropCoffeeshopLocation(mongoTemplate);
    }

    @Test
    void getNearestCoffeeShopWhenFoundShouldReturnLocation() {
        EntityExchangeResult<String> response = webTestClient.get() //
            .uri(SERVICE_PATH, 59.969048, 10.774445, 2500) //
            .accept(MediaType.APPLICATION_JSON) //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE) //
            .expectBody(String.class) //
            .consumeWith(result -> {
                assertThat(result.getResponseBody()).contains("coordinates");
            }) //
            .returnResult();

        System.err.println(response);
    }

    @Test
    void getNearestCoffeeShopWhenNotFoundShouldReturnNoContent() {
        webTestClient.get() //
            .uri(SERVICE_PATH, 59.969048, 10.774445, 5) //
            .accept(MediaType.APPLICATION_JSON) //
            .exchange() //
            .expectStatus().isNoContent();
    }
}
