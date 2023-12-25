package study.microcoffee.location.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import study.microcoffee.location.test.utils.MongoDBUtils;

/**
 * Integration tests of {@link LocationController} based on {@link RestClient}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.ssl.enabled=false")
@TestPropertySource("/application-test.properties")
@ActiveProfiles("itest")
@Profile("itest")
@DirtiesContext // Fixes "Address already in use: bind" for port 8081
class LocationControllerRestClientIT {

    private static final String SERVICE_PATH = "/api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private MongoTemplate mongoTemplate;

    private RestClient restClient;

    @BeforeEach
    void init() throws Exception {
        MongoDBUtils.loadCoffeeshopCollection(mongoTemplate, "testdata/coffeeshop.json");

        restClient = RestClient.builder().baseUrl("http://localhost:" + serverPort).build();
    }

    @AfterEach
    void destroy() {
        MongoDBUtils.dropCoffeeshopLocation(mongoTemplate);
    }

    @Test
    void getNearestCoffeeShopWhenFoundShouldReturnLocation() {
        ResponseEntity<String> response = restClient.get() //
            .uri(SERVICE_PATH, 59.969048, 10.774445, 2500) //
            .retrieve() //
            .toEntity(String.class);

        System.err.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("coordinates");
        assertThat(response.getHeaders().getContentType()).asString().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void getNearestCoffeeShopWhenNotFoundShouldReturnNoContent() {
        ResponseEntity<String> response = restClient.get() //
            .uri(SERVICE_PATH, 59.969048, 10.774445, 5) //
            .retrieve() //
            .toEntity(String.class);

        System.err.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
